package com.baomidou.shaun.core.util;

import com.baomidou.shaun.core.context.JEEContextFactory;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.SessionStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author miemie
 * @since 2019-07-20
 */
@SuppressWarnings("unchecked")
public abstract class JEEContextUtil {

    public static JEEContext getJEEContext(final JEEContextFactory jeeContextFactory, final SessionStore<JEEContext> sessionStore) {
        return jeeContextFactory.applyContext(request(), response(), sessionStore);
    }

    public static HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse response() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }
}
