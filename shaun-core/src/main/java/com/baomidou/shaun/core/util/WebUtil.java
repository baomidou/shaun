package com.baomidou.shaun.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.baomidou.shaun.core.context.session.NoSessionStore;

import lombok.Setter;

/**
 * @author miemie
 * @since 2020-05-26
 */
public abstract class WebUtil {

    @Setter
    private static boolean enableSession = false;

    /**
     * 获取 ServletRequestAttributes
     *
     * @return ServletRequestAttributes
     */
    public static ServletRequestAttributes getServletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    /**
     * 获取 request
     *
     * @return request
     */
    public static HttpServletRequest getRequestBySpringWebHolder() {
        return getServletRequestAttributes().getRequest();
    }

    /**
     * 获取 response
     *
     * @return response
     */
    public static HttpServletResponse getResponseBySpringWebHolder() {
        return getServletRequestAttributes().getResponse();
    }

    /**
     * 重定向到指定页面
     *
     * @param context 上下文
     * @param url     地址
     */
    public static void redirectUrl(JEEContext context, String url) {
        redirectUrl(context.getNativeResponse(), url);
    }

    /**
     * 重定向到指定页面
     *
     * @param response HttpServletResponse
     * @param url      地址
     */
    public static void redirectUrl(HttpServletResponse response, String url) {
        response.setHeader(HttpConstants.LOCATION_HEADER, url);
        response.setStatus(HttpConstants.FOUND);
    }

    public static JEEContext getJEEContext() {
        ServletRequestAttributes sra = getServletRequestAttributes();
        return getJEEContext(sra.getRequest(), sra.getResponse());
    }

    public static JEEContext getJEEContext(final HttpServletRequest request, final HttpServletResponse response) {
        return new JEEContext(request, response, enableSession ? JEESessionStore.INSTANCE : NoSessionStore.INSTANCE);
    }
}
