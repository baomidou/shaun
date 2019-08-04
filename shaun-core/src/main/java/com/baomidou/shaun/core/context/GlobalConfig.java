package com.baomidou.shaun.core.context;

import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.http.ajax.AjaxRequestResolver;

/**
 * 全局快捷工具类
 *
 * @author miemie
 * @since 2019-08-03
 */
public class GlobalConfig {

    /**
     * 是否是前后端分离的
     */
    private static boolean stateless = true;
    /**
     * 登录页面
     */
    private static String loginUrl;
    /**
     * index url
     * 三分登录的回调成功后 redirect 的主页
     */
    private String indexUrl;
    /**
     * ajax 判断器
     */
    private static AjaxRequestResolver ajaxRequestResolver;

    public static boolean isStateless() {
        return stateless;
    }

    public static void setStateless(boolean stateless) {
        GlobalConfig.stateless = stateless;
    }

    public static String getLoginUrl() {
        return loginUrl;
    }

    public static void setLoginUrl(String loginUrl) {
        GlobalConfig.loginUrl = loginUrl;
    }

    public static void setAjaxRequestResolver(AjaxRequestResolver ajaxRequestResolver) {
        GlobalConfig.ajaxRequestResolver = ajaxRequestResolver;
    }

    public static AjaxRequestResolver getAjaxRequestResolver() {
        return ajaxRequestResolver;
    }

    public static boolean isStatelessOrAjax(JEEContext context) {
        return stateless && !ajaxRequestResolver.isAjax(context);
    }

    public static void gotoLoginUrl(JEEContext context) {
        gotoUrl(context, loginUrl);
    }

    public static void gotoUrl(JEEContext context, String url) {
        context.setResponseHeader(HttpConstants.LOCATION_HEADER, url);
        HttpServletResponse response = context.getNativeResponse();
        response.setStatus(HttpConstants.FOUND);
    }
}
