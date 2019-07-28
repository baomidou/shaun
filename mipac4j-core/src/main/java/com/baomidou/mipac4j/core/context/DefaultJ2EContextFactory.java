package com.baomidou.mipac4j.core.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * @author miemie
 * @since 2019-07-20
 */
public class DefaultJ2EContextFactory implements J2EContextFactory {

    public static final DefaultJ2EContextFactory INSTANCE = new DefaultJ2EContextFactory();

    @Override
    public J2EContext applyContext(HttpServletRequest request, HttpServletResponse response, SessionStore<J2EContext> sessionStore) {
        return new IJ2EContext(request, response, sessionStore);
    }

    /**
     * 只重写 J2EContext 的 setResponseStatus
     */
    public static final class IJ2EContext extends J2EContext {

        public IJ2EContext(HttpServletRequest request, HttpServletResponse response, SessionStore<J2EContext> sessionStore) {
            super(request, response, sessionStore);
        }

        @Override
        public void setResponseStatus(int code) {
            this.getResponse().setStatus(code);
        }
    }
}
