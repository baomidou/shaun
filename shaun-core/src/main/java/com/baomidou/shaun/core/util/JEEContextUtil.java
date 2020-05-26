package com.baomidou.shaun.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.JEESessionStore;

import com.baomidou.shaun.core.context.session.NoSessionStore;

/**
 * @author miemie
 * @since 2019-07-20
 */
public abstract class JEEContextUtil {

    private static boolean enableSession = false;

    public static JEEContext getJEEContext() {
        return getJEEContext(WebUtil.getRequestBySpringWebHolder(), WebUtil.getResponseBySpringWebHolder());
    }

    public static JEEContext getJEEContext(final HttpServletRequest request, final HttpServletResponse response) {
        return new JEEContext(request, response, enableSession ? JEESessionStore.INSTANCE : NoSessionStore.INSTANCE);
    }

    public static void setEnableSession(boolean enableSession) {
        JEEContextUtil.enableSession = enableSession;
    }
}
