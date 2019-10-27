package com.baomidou.shaun.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.baomidou.shaun.core.enums.Logical;

/**
 * @author miemie
 * @since 2019-06-29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasAuthorization {

    HasRole roles();

    HasPermission permissions();

    Logical logical() default Logical.OR;
}
