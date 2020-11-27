package com.baomidou.shaun.core.config;

import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.credentials.TokenLocation;
import com.baomidou.shaun.core.credentials.location.Cookie;
import com.baomidou.shaun.core.handler.DefaultHttpActionHandler;
import com.baomidou.shaun.core.handler.DefaultLogoutHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.mgt.ProfileJwtManager;
import com.baomidou.shaun.core.mgt.ProfileStateManager;
import com.baomidou.shaun.core.util.WebUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.matching.checker.DefaultMatchingChecker;
import org.pac4j.core.matching.checker.MatchingChecker;
import org.pac4j.core.matching.matcher.DefaultMatchers;
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
@Data
@SuppressWarnings("all")
public class ShaunConfig {

    /**
     * 是否-前后端分离
     */
    private boolean stateless = true;
    /**
     * 是否-启用session
     */
    private boolean sessionOn = false;
    /**
     * token 位置
     */
    private TokenLocation tokenLocation;
    /**
     * 超时时间
     */
    private String expireTime;
    /**
     * profile 管理器
     */
    private ProfileJwtManager profileJwtManager;
    /**
     * profile 状态管理器
     */
    private ProfileStateManager profileStateManager = ProfileStateManager.DEFAULT;
    /**
     * 用户 role 和 permission 授权,鉴权 类
     */
    private AuthorityManager authorityManager;
    /**
     * 登出执行器
     */
    private LogoutHandler logoutHandler = new DefaultLogoutHandler();
    /**
     * cookie 配置
     */
    private Cookie cookie;
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
     * 登录页地址
     * <p>
     * 会自动加入地址过滤链,避免请求该地址被拦截
     */
    private String loginUrl;

    public void addAuthorizer(Authorizer authorizer) {
        if (authorizer != null) {
            String key = authorizer.getClass().getSimpleName();
            authorizersMap.put(key, authorizer);
            this.authorizerNamesAppend(key);
        }
    }

    public void addAuthorizers(Collection<Authorizer> authorizers) {
        if (!CollectionUtils.isEmpty(authorizers)) {
            for (Authorizer authorizer : authorizers) {
                addAuthorizer(authorizer);
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

    public void addMatcher(Matcher matcher) {
        if (matcher != null) {
            String key = matcher.getClass().getSimpleName();
            matchersMap.put(key, matcher);
            this.matcherNamesAppend(key);
        }
    }

    public void addMatchers(Collection<Matcher> matchers) {
        if (!CollectionUtils.isEmpty(matchers)) {
            for (Matcher matcher : matchers) {
                addMatcher(matcher);
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
     * 重定向到登录页面
     *
     * @param context 上下文
     */
    public void redirectLoginUrl(JEEContext context) {
        WebUtil.redirectUrl(context, loginUrl);
    }
}
