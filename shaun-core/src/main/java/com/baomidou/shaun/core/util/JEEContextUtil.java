package com.baomidou.shaun.core.util;

import com.baomidou.shaun.core.context.session.NoSessionStore;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author miemie
 * @since 2019-07-20
 */
public abstract class JEEContextUtil {

    private static boolean enableSession = false;

    public static JEEContext getJEEContext() {
        return getJEEContext(request(), response());
    }

    public static JEEContext getJEEContext(final HttpServletRequest request, final HttpServletResponse response) {
        return new JEEContext(request, response, enableSession ? JEESessionStore.INSTANCE : NoSessionStore.INSTANCE);
    }

    public static HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse response() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    public static void setEnableSession(boolean enableSession) {
        JEEContextUtil.enableSession = enableSession;
    }

    /**
     * 重定向到指定页面
     *
     * @param url 地址
     */
    public static void redirectUrl(String url) {
        redirectUrl(getJEEContext(), url);
    }

    /**
     * 重定向到指定页面
     *
     * @param context 上下文
     * @param url     地址
     */
    public static void redirectUrl(JEEContext context, String url) {
        context.setResponseHeader(HttpConstants.LOCATION_HEADER, url);
        context.getNativeResponse().setStatus(HttpConstants.FOUND);
    }
}
