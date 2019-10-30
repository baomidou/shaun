package com.baomidou.shaun.core.annotation;

import com.baomidou.shaun.core.enums.Logical;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author miemie
 * @since 2019-06-29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasPermission {

    String[] value();

    Logical logical() default Logical.ANY;
}
