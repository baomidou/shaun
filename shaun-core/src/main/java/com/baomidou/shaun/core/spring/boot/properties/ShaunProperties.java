package com.baomidou.shaun.core.spring.boot.properties;

import java.util.List;
import java.util.UUID;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.CsrfAuthorizer;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.matching.matcher.CacheControlMatcher;
import org.pac4j.core.matching.matcher.DefaultMatchers;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.StrictTransportSecurityMatcher;
import org.pac4j.core.matching.matcher.XContentTypeOptionsMatcher;
import org.pac4j.core.matching.matcher.XFrameOptionsMatcher;
import org.pac4j.core.matching.matcher.XSSProtectionMatcher;
import org.pac4j.core.matching.matcher.csrf.CsrfTokenGeneratorMatcher;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.baomidou.shaun.core.authorization.DefaultAuthorizationChecker;
import com.baomidou.shaun.core.context.Cookie;
import com.baomidou.shaun.core.enums.Model;
import com.baomidou.shaun.core.matching.checker.DefaultMatchingChecker;

import lombok.Data;

/**
 * @author miemie
 * @since 2019-07-01
 */
@Data
public class ShaunProperties {

    /**
     * 取 token 的方式之 cookie
     */
    @NestedConfigurationProperty
    private final Cookie cookie = new Cookie();
    /**
     * 拦截url的模式
     */
    private Model model = Model.INTERCEPTOR;
    /**
     * authorizerNames,多个以逗号分隔(不包含自己注入的 {@link Authorizer}),
     * !!! 以下 {@link #excludePath} 和 {@link #excludeBranch} 和 {@link #excludeRegex} 排除掉的之外的地址都生效 !!!,
     * 默认支持的一些参考 {@link DefaultAuthorizationChecker} :
     * <p>
     * csrfCheck : {@link CsrfAuthorizer} ,
     * </p>
     */
    private String authorizerNames = DefaultAuthorizers.NONE;
    /**
     * matcherNames,多个以逗号分隔(不包含自己注入的 {@link Matcher}),
     * !!! 全部地址都生效 !!! ,
     * 默认支持的一些参考 {@link DefaultMatchingChecker} :
     * <p>
     * hsts : {@link StrictTransportSecurityMatcher} ,
     * nosniff : {@link XContentTypeOptionsMatcher} ,
     * noframe : {@link XFrameOptionsMatcher} ,
     * xssprotection : {@link XSSProtectionMatcher} ,
     * nocache : {@link CacheControlMatcher} ,
     * csrfToken : {@link CsrfTokenGeneratorMatcher} ,
     * noGet : 不接受get请求(post,delete,put) ,
     * onlyPost : 只接受post请求 ,
     * securityheaders : 等于上面的 nocache + nosniff + hsts + noframe + xssprotection
     * </p>
     */
    private String matcherNames = DefaultMatchers.NONE;
    /**
     * 跳过鉴权的 role 和 permission 的表现字符串(相当于系统超管)
     */
    private String skipAuthenticationRolePermission = "shaun-admin-role-permission";
    /**
     * 排除的 url
     */
    private List<String> excludePath;
    /**
     * 排除的 url 的统一前缀
     */
    private List<String> excludeBranch;
    /**
     * 排除的 url 的 正则表达式
     */
    private List<String> excludeRegex;
    /**
     * jwt 加密盐值(默认加密方式只支持 32 位字符)
     */
    private String salt = UUID.randomUUID().toString().replace("-", "");
    /**
     * jwt 超时时间
     * <li> 10s : 表示10秒有效 </li>
     * <li> 10m 结尾: 表示10分钟有效 </li>
     * <li> 10h 结尾: 表示10小时有效 </li>
     * <p>
     * <li> 1d : 表示有效时间到第二天 00:00:00  </li>
     * <li> 2d1h : 表示有效时间到第二天 01:00:00 </li>
     * <b> `d` 后面 只支持上面三个(`s`,`m`,`h`)之一 </b>
     *
     * <p>
     * 纯 cookie 模式下可以不设置,则cookie过期时间为会话时间但是token永不过期
     * </p>
     */
    private String expireTime;
    /**
     * 登出 url
     * <p>
     * <p>
     * 如果是ajax请求则不做处理,否者会在登出后重定向到登录页
     * </p>
     */
    private String logoutUrl;
}
