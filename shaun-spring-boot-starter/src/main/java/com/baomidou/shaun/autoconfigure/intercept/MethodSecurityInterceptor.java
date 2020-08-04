package com.baomidou.shaun.autoconfigure.intercept;

import com.baomidou.shaun.core.annotation.HasAuthorization;
import com.baomidou.shaun.core.annotation.HasPermission;
import com.baomidou.shaun.core.annotation.HasRole;
import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.enums.Logical;
import com.baomidou.shaun.core.profile.TokenProfile;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
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
 * 注解优先级:
 * <p> method &gt; type </p>
 * <p> HasRole &gt; HasPermission &gt; HasAuthorization </p>
 *
 * <p> 注意: 只会命中一个注解! </p>
 *
 * @author miemie
 * @since 2020-05-19
 */
@Slf4j
public class MethodSecurityInterceptor implements MethodInterceptor, ApplicationContextAware {

    private AuthorityManager authorityManager;
    private ApplicationContext context;

    private void initAuthorityManager() {
        if (authorityManager == null) {
            authorityManager = context.getBean(AuthorityManager.class);
        }
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.initAuthorityManager();
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
                return UnauthorizedAction.INSTANCE;
            }
            if (!authorityManager.isSkipAuthenticationUser(profiles)) {
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
            return UnauthorizedAction.INSTANCE;
        }
        if (authorityManager.isSkipAuthenticationUser(profiles)) {
            return null;
        }
        return toCheck(profiles, isRole, logical, elements, checkValues);
    }

    private HttpAction toCheck(final TokenProfile profiles, final boolean isRole, final Logical logical,
                               final Set<String> elements, final Function<TokenProfile, Set<String>> checkValues) {
        if (isRole) {
            if (!authorityManager.checkRoles(logical, elements, checkValues.apply(profiles))) {
                return ForbiddenAction.INSTANCE;
            }
        }
        if (!authorityManager.checkPermissions(logical, elements, checkValues.apply(profiles))) {
            return ForbiddenAction.INSTANCE;
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
}
