package com.baomidou.shaun.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author miemie
 * @since 2020-05-26
 */
public abstract class WebUtil {

    /**
     * 获取 request
     *
     * @return request
     */
    @SuppressWarnings("all")
    public static HttpServletRequest getRequestBySpringWebHolder() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取 response
     *
     * @return response
     */
    @SuppressWarnings("all")
    public static HttpServletResponse getResponseBySpringWebHolder() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
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
}
