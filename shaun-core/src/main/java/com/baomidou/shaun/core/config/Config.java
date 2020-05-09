package com.baomidou.shaun.core.config;

import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.handler.DefaultHttpActionHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.matching.checker.DefaultMatchingChecker;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author miemie
 * @since 2020-04-29
 */
@SuppressWarnings("all")
public class Config {

    /**
     * client
     */
    @Setter
    @Getter
    private TokenClient tokenClient;
    /**
     * 是否是前后端分离的
     */
    @Setter
    @Getter
    private boolean stateless = true;
    /**
     * 处理抛出的异常
     */
    @Setter
    @Getter
    private HttpActionHandler httpActionHandler = new DefaultHttpActionHandler();
    /**
     * 回调处理器用来发现client
     */
    @Setter
    @Getter
    private ClientFinder clientFinder = new DefaultCallbackClientFinder();
    /**
     * 登录页面
     */
    @Setter
    private String loginUrl;
    /**
     * ajax 判断器
     */
    @Setter
    @Getter
    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
    /**
     * 默认支持的一些参考 {@link DefaultAuthorizationChecker}
     */
    @Getter
    private String authorizerNames;
    /**
     * 默认支持的一些参考 {@link DefaultMatchingChecker}
     */
    @Getter
    private String matcherNames;

    @Getter
    private Map<String, Authorizer> authorizersMap = new HashMap<>();

    @Getter
    private Map<String, Matcher> matchersMap = new HashMap<>();

    public void addAuthorizers(Collection<Authorizer> authorizers) {
        if (!CollectionUtils.isEmpty(authorizers)) {
            for (Authorizer authorizer : authorizers) {
                String key = authorizer.getClass().getSimpleName();
                authorizersMap.put(key, authorizer);
                this.authorizerNamesAppend(key);
            }
        }
    }

    public void authorizerNamesAppend(String value) {
        if (CommonHelper.isNotBlank(value)) {
            if (authorizerNames == null) {
                authorizerNames = value;
            } else {
                authorizerNames = authorizerNames.concat(Pac4jConstants.ELEMENT_SEPARATOR).concat(value);
            }
        }
    }

    public void addMatchers(Collection<Matcher> matchers) {
        if (!CollectionUtils.isEmpty(matchers)) {
            for (Matcher matcher : matchers) {
                String key = matcher.getClass().getSimpleName();
                matchersMap.put(key, matcher);
                this.matcherNamesAppend(key);
            }
        }
    }

    public void matcherNamesAppend(String value) {
        if (CommonHelper.isNotBlank(value)) {
            if (matcherNames == null) {
                matcherNames = value;
            } else {
                matcherNames = matcherNames.concat(Pac4jConstants.ELEMENT_SEPARATOR).concat(value);
            }
        }
    }

    /**
     * 判断请求是否是 ajax 的
     */
    public boolean isAjax(JEEContext context) {
        return ajaxRequestResolver.isAjax(context);
    }

    /**
     * 判断请求是否是前后分离下的 或者是 ajax 的
     */
    public boolean isStatelessOrAjax(JEEContext context) {
        return stateless || ajaxRequestResolver.isAjax(context);
    }

    /**
     * 重定向到登录页面
     *
     * @param context 上下文
     */
    public void redirectLoginUrl(JEEContext context) {
        redirectUrl(context, loginUrl);
    }

    /**
     * 重定向到指定页面
     *
     * @param context 上下文
     * @param url     地址
     */
    public void redirectUrl(JEEContext context, String url) {
        context.setResponseHeader(HttpConstants.LOCATION_HEADER, url);
        context.getNativeResponse().setStatus(HttpConstants.FOUND);
    }
}
