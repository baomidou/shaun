package com.baomidou.mipac4j.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.baomidou.mipac4j.core.context.J2EContextFactory;

/**
 * @author miemie
 * @since 2019-07-20
 */
public abstract class J2EContextUtil {

    @SuppressWarnings("all")
    public static J2EContext getJ2EContext(final J2EContextFactory j2EContextFactory, SessionStore sessionStore) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        return j2EContextFactory.applyContext(request, response, sessionStore);
    }
}
