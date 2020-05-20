package com.baomidou.shaun.autoconfigure.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

import com.baomidou.shaun.core.annotation.HasAuthorization;
import com.baomidou.shaun.core.annotation.HasPermission;
import com.baomidou.shaun.core.annotation.HasRole;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 注解优先级:
 * <p> method > type </p>
 * <p> HasRole > HasPermission > HasAuthorization </p>
 *
 * <p> 注意: 只会命中一个注解! </p>
 * copy from {org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor}
 *
 * @author miemie
 * @since 2020-05-19
 */
@Slf4j
@SuppressWarnings("unchecked")
@AllArgsConstructor
public class MethodSecurityAdvisor extends StaticMethodMatcherPointcutAdvisor {

    private static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES =
            new Class[]{
                    HasRole.class, HasPermission.class,
                    HasAuthorization.class
            };


    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        Method m = method;

        if (isAuthzAnnotationPresent(m)) {
            return true;
        }

        //The 'method' parameter could be from an interface that doesn't have the annotation.
        //Check to see if the implementation has it.
        if (targetClass != null) {
            try {
                m = targetClass.getMethod(m.getName(), m.getParameterTypes());
                return isAuthzAnnotationPresent(m) || isAuthzAnnotationPresent(targetClass);
            } catch (NoSuchMethodException ignored) {
                //default return value is false.  If we can't find the method, then obviously
                //there is no annotation, so just use the default return value.
            }
        }

        return false;
    }

    private boolean isAuthzAnnotationPresent(Class<?> targetClazz) {
        for (Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES) {
            Annotation a = AnnotationUtils.findAnnotation(targetClazz, annClass);
            if (a != null) {
                return true;
            }
        }
        return false;
    }

    private boolean isAuthzAnnotationPresent(Method method) {
        for (Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES) {
            Annotation a = AnnotationUtils.findAnnotation(method, annClass);
            if (a != null) {
                return true;
            }
        }
        return false;
    }
}
