package com.baomidou.shaun.core.enums;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截模式
 *
 * @author miemie
 * @since 2019-08-08
 */
public enum Model {

    /**
     * spring 的 {@link HandlerInterceptor}
     */
    INTERCEPTOR,
    /**
     * spring 的 {@link OncePerRequestFilter}
     */
    WEB_FILTER
}
