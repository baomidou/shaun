package com.baomidou.shaun.core.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.matching.checker.MatchingChecker;
import org.pac4j.core.matching.matcher.DefaultMatchers;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.springframework.util.CollectionUtils;

import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.authorization.DefaultAuthorizationChecker;
import com.baomidou.shaun.core.context.Cookie;
import com.baomidou.shaun.core.handler.DefaultHttpActionHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.matching.checker.DefaultMatchingChecker;
import com.baomidou.shaun.core.mgt.ProfileManager;
import com.baomidou.shaun.core.util.WebUtil;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * @author miemie
 * @since 2020-04-29
 */
@Data
@SuppressWarnings("all")
public class Config {

    /**
     * 超时时间
     */
    private String expireTime;
    /**
     * profile 管理器
     */
    private ProfileManager profileManager;
    /**
     * profile 管理器
     */
    private AuthorityManager authorityManager;
    /**
     * 登出执行器
     */
    private LogoutHandler logoutHandler;
    /**
     * cookie 配置
     */
    private Cookie cookie;
    /**
     * 是否是前后端分离的
     */
    private boolean stateless = true;
    /**
     * 处理抛出的异常
     */
    private HttpActionHandler httpActionHandler = new DefaultHttpActionHandler();
    /**
     * 回调处理器用来发现client
     */
    private ClientFinder clientFinder = new DefaultCallbackClientFinder();
    /**
     * ajax 判断器
     */
    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
    /**
     * AuthorizationChecker
     */
    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();
    /**
     * MatchingChecker
     */
    private MatchingChecker matchingChecker = new DefaultMatchingChecker();
    /**
     * 默认支持的一些参考 {@link DefaultAuthorizationChecker}
     */
    @Setter(AccessLevel.NONE)
    private String authorizerNames;
    /**
     * 默认支持的一些参考 {@link DefaultMatchingChecker}
     */
    @Setter(AccessLevel.NONE)
    private String matcherNames;

    @Setter(AccessLevel.NONE)
    private Map<String, Authorizer> authorizersMap = new HashMap<>();

    @Setter(AccessLevel.NONE)
    private Map<String, Matcher> matchersMap = new HashMap<>();


    /**
     * 登录页面
     */
    private String loginUrl;

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
                if (DefaultAuthorizers.NONE.equals(authorizerNames)) {
                    authorizerNames = value;
                } else {
                    if (!DefaultAuthorizers.NONE.equals(value)) {
                        authorizerNames = authorizerNames.concat(Pac4jConstants.ELEMENT_SEPARATOR).concat(value);
                    }
                }
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
                if (DefaultMatchers.NONE.equals(matcherNames)) {
                    matcherNames = value;
                } else {
                    if (!DefaultMatchers.NONE.equals(value)) {
                        matcherNames = matcherNames.concat(Pac4jConstants.ELEMENT_SEPARATOR).concat(value);
                    }
                }
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
        WebUtil.redirectUrl(context, loginUrl);
    }
}
