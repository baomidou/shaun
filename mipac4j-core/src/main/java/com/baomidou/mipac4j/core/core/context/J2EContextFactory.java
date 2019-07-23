package com.baomidou.mipac4j.core.core.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * @author miemie
 * @since 2019-07-20
 */
public interface J2EContextFactory {

    /**
     * 构建 J2EContext
     *
     * @param request      request
     * @param response     response
     * @param sessionStore sessionStore
     * @return J2EContext
     */
    J2EContext applyContext(HttpServletRequest request, HttpServletResponse response, SessionStore<J2EContext> sessionStore);
}
