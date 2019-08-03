package com.baomidou.shaun.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.JEEContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.baomidou.shaun.core.context.session.NoSessionStore;

/**
 * @author miemie
 * @since 2019-07-20
 */
public abstract class JEEContextFactory {

    public static JEEContext getJEEContext() {
        return getJEEContext(request(), response());
    }

    public static JEEContext getJEEContext(final HttpServletRequest request, final HttpServletResponse response) {
        return new JEEContext(request, response, NoSessionStore.INSTANCE);
    }

    public static HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse response() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }
}
