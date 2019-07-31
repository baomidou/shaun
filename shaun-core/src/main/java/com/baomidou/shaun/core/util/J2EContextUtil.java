package com.baomidou.shaun.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.baomidou.shaun.core.context.J2EContextFactory;

/**
 * @author miemie
 * @since 2019-07-20
 */
@SuppressWarnings("unchecked")
public abstract class J2EContextUtil {

    public static J2EContext getJ2EContext(final J2EContextFactory j2EContextFactory, final SessionStore<J2EContext> sessionStore) {
        return j2EContextFactory.applyContext(request(), response(), sessionStore);
    }

    public static HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse response() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }
}
