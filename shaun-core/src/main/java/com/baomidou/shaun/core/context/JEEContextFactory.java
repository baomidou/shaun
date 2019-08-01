package com.baomidou.shaun.core.context;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.SessionStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author miemie
 * @since 2019-07-20
 */
public interface JEEContextFactory {

    /**
     * 构建 J2EContext
     *
     * @param request      request
     * @param response     response
     * @param sessionStore sessionStore
     * @return J2EContext
     */
    JEEContext applyContext(HttpServletRequest request, HttpServletResponse response, SessionStore<JEEContext> sessionStore);
}
