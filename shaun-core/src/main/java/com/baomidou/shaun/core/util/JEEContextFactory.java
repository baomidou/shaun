package com.baomidou.shaun.core.util;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author miemie
 * @since 2019-07-20
 */
public abstract class JEEContextFactory {

    public static final SessionStore<JEEContext> sessionStore = new JEESessionStore();

    public static JEEContext getJEEContext() {
        return getJEEContext(request(), response(), sessionStore);
    }

    public static JEEContext getJEEContext(final SessionStore<JEEContext> sessionStore) {
        return getJEEContext(request(), response(), sessionStore);
    }

    public static JEEContext getJEEContext(final HttpServletRequest request, final HttpServletResponse response) {
        return new JEEContext(request, response, sessionStore);
    }

    public static JEEContext getJEEContext(final HttpServletRequest request, final HttpServletResponse response,
                                           final SessionStore<JEEContext> sessionStore) {
        return new JEEContext(request, response, sessionStore);
    }

    public static HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse response() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }
}
