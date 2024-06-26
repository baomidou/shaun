/*
 * Copyright 2019-2024 baomidou (wonderming@vip.qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.core.aop;

import com.baomidou.shaun.core.annotation.HasAuthorization;
import com.baomidou.shaun.core.annotation.HasPermission;
import com.baomidou.shaun.core.annotation.HasRole;
import com.baomidou.shaun.core.annotation.Logical;
import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.HttpActionInstance;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.util.InitializableObject;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * 注解优先级: <br>
 * method &gt; type <br>
 * HasRole &gt; HasPermission &gt; HasAuthorization <br>
 * <p>
 * 注意: 只会命中一个注解!
 *
 * @author miemie
 * @since 2020-05-19
 */
@Slf4j
public class MethodSecurityInterceptor extends InitializableObject implements MethodInterceptor, ApplicationContextAware {

    private AuthorityManager authorityManager;
    private ApplicationContext context;

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        init();
        HttpAction action = decide(mi);
        if (action != null) {
            throw action;
        }
        return mi.proceed();
    }

    private HttpAction decide(MethodInvocation mi) {
        Object target = mi.getThis();
        Class<?> targetClass = null;

        if (target != null) {
            targetClass = target instanceof Class<?> ? (Class<?>) target : AopProxyUtils.ultimateTargetClass(target);
        }

        HasRole hasRole = findAnnotation(mi.getMethod(), targetClass, HasRole.class);
        if (hasRole != null) {
            return commonAuthorized(true, hasRole.logical(), toSet(hasRole.value()), authorityManager::roles);
        }

        HasPermission hasPermission = findAnnotation(mi.getMethod(), targetClass, HasPermission.class);
        if (hasPermission != null) {
            return commonAuthorized(false, hasPermission.logical(), toSet(hasPermission.value()), authorityManager::permissions);
        }

        HasAuthorization hasAuthorization = findAnnotation(mi.getMethod(), targetClass, HasAuthorization.class);
        if (hasAuthorization != null) {
            final Logical logical = hasAuthorization.logical();
            final HasRole role = hasAuthorization.role();
            final Set<String> roles = toSet(role.value());
            final HasPermission permission = hasAuthorization.permission();
            final Set<String> permissions = toSet(permission.value());
            final TokenProfile profiles = ProfileHolder.getProfile();
            if (profiles == null) {
                log.debug("not found TokenProfile, so authorization not success!");
                return HttpActionInstance.UNAUTHORIZED;
            }
            if (!authorityManager.isSkipAuthentication(profiles)) {
                HttpAction action = toCheck(profiles, true, role.logical(), roles, authorityManager::roles);
                if (logical == Logical.ANY) {
                    if (action == null) {
                        return null;
                    }
                } else {
                    if (action != null) {
                        return action;
                    }
                }
                return toCheck(profiles, false, permission.logical(), permissions, authorityManager::permissions);
            }
        }
        return null;
    }

    private HttpAction commonAuthorized(final boolean isRole, final Logical logical,
                                        final Set<String> elements,
                                        final Function<TokenProfile, Set<String>> checkValues) {
        final TokenProfile profiles = ProfileHolder.getProfile();
        if (profiles == null) {
            log.debug("not found TokenProfile, so authorization not success!");
            return HttpActionInstance.UNAUTHORIZED;
        }
        if (authorityManager.isSkipAuthentication(profiles)) {
            return null;
        }
        return toCheck(profiles, isRole, logical, elements, checkValues);
    }

    private HttpAction toCheck(final TokenProfile profiles, final boolean isRole, final Logical logical,
                               final Set<String> elements, final Function<TokenProfile, Set<String>> checkValues) {
        if (isRole) {
            if (!authorityManager.checkRoles(logical, elements, checkValues.apply(profiles))) {
                return HttpActionInstance.FORBIDDEN;
            }
        }
        if (!authorityManager.checkPermissions(logical, elements, checkValues.apply(profiles))) {
            return HttpActionInstance.FORBIDDEN;
        }
        return null;
    }

    /**
     * copy from {org.springframework.security.access.prepost.PrePostAnnotationSecurityMetadataSource#findAnnotation}
     */
    private <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass,
                                                    Class<A> annotationClass) {
        // The method may be on an interface, but we need attributes from the target
        // class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        A annotation = AnnotationUtils.findAnnotation(specificMethod, annotationClass);

        if (annotation != null) {
            log.debug(annotation + " found on specific method: " + specificMethod);
            return annotation;
        }

        // Check the original (e.g. interface) method
        if (specificMethod != method) {
            annotation = AnnotationUtils.findAnnotation(method, annotationClass);

            if (annotation != null) {
                log.debug(annotation + " found on: " + method);
                return annotation;
            }
        }

        // Check the class-level (note declaringClass, not targetClass, which may not
        // actually implement the method)
        annotation = AnnotationUtils.findAnnotation(specificMethod.getDeclaringClass(), annotationClass);

        if (annotation != null) {
            log.debug(annotation + " found on: " + specificMethod.getDeclaringClass().getName());
            return annotation;
        }

        return null;
    }

    private Set<String> toSet(String[] values) {
        return new HashSet<>(Arrays.asList(values));
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    protected void internalInit(boolean forceReinit) {
        if (authorityManager == null) {
            authorityManager = context.getBean(AuthorityManager.class);
        }
    }
}
