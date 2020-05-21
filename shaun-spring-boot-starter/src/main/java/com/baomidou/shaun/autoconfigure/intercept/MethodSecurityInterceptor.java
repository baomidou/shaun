package com.baomidou.shaun.autoconfigure.intercept;

import com.baomidou.shaun.core.annotation.HasAuthorization;
import com.baomidou.shaun.core.annotation.HasPermission;
import com.baomidou.shaun.core.annotation.HasRole;
import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.enums.Logical;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.JEEContextFactory;
import com.baomidou.shaun.core.util.ProfileHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * 注解优先级:
 * <p> method > type </p>
 * <p> HasRole > HasPermission > HasAuthorization </p>
 *
 * <p> 注意: 只会命中一个注解! </p>
 *
 * @author miemie
 * @since 2020-05-19
 */
@Slf4j
@AllArgsConstructor
public class MethodSecurityInterceptor implements MethodInterceptor {

    private final AuthorityManager authorityManager;

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        boolean decide = decide(mi);
        if (!decide) {
            throw UnauthorizedAction.INSTANCE;
        }
        return mi.proceed();
    }

    public boolean decide(MethodInvocation mi) {
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
            JEEContext j2EContext = JEEContextFactory.getJEEContext();
            final TokenProfile profiles = ProfileHolder.getProfile(j2EContext);
            if (profiles == null) {
                log.debug("not found TokenProfile, so authorization not success!");
                return false;
            }
            if (!authorityManager.isSkipAuthenticationUser(profiles)) {
                if (logical == Logical.ANY) {
                    return toCheck(profiles, true, role.logical(), roles, authorityManager::roles)
                            || toCheck(profiles, false, permission.logical(), permissions, authorityManager::permissions);
                } else {
                    return toCheck(profiles, true, role.logical(), roles, authorityManager::roles)
                            && toCheck(profiles, false, permission.logical(), permissions, authorityManager::permissions);
                }
            }
        }
        return true;
    }

    private boolean commonAuthorized(final boolean isRole, final Logical logical,
                                     final Set<String> elements,
                                     final Function<TokenProfile, Set<String>> checkValues) {
        JEEContext j2EContext = JEEContextFactory.getJEEContext();
        final TokenProfile profiles = ProfileHolder.getProfile(j2EContext);
        if (profiles == null) {
            log.debug("not found TokenProfile, so authorization not success!");
            return false;
        }
        if (authorityManager.isSkipAuthenticationUser(profiles)) {
            return true;
        }
        return toCheck(profiles, isRole, logical, elements, checkValues);
    }

    private boolean toCheck(final TokenProfile profiles, final boolean isRole, final Logical logical,
                            final Set<String> elements, final Function<TokenProfile, Set<String>> checkValues) {
        if (isRole) {
            return authorityManager.checkRoles(logical, elements, checkValues.apply(profiles));
        }
        return authorityManager.checkPermissions(logical, elements, checkValues.apply(profiles));
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
}