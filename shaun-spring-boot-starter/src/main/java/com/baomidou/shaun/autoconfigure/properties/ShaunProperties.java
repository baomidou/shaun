package com.baomidou.shaun.autoconfigure.properties;

import com.baomidou.shaun.core.credentials.TokenLocation;
import com.baomidou.shaun.core.credentials.location.Cookie;
import com.baomidou.shaun.core.credentials.location.Header;
import com.baomidou.shaun.core.credentials.location.Parameter;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.intercept.InterceptModel;
import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.profile.TokenProfile;
import lombok.Data;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.matching.checker.DefaultMatchingChecker;
import org.pac4j.core.matching.matcher.DefaultMatchers;
import org.pac4j.core.matching.matcher.Matcher;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;
import java.util.UUID;

/**
 * @author miemie
 * @since 2020-11-09
 */
@Data
@ConfigurationProperties("shaun")
public class ShaunProperties {

    /**
     * 只是为了在 yml 有提示
     */
    private final Annotations annotations = new Annotations();
    /**
     * 是否-前后端分离
     */
    private boolean stateless = true;
    /**
     * 是否-启用session
     */
    private boolean sessionOn = false;
    /**
     * token 的存放位置
     */
    private TokenLocation tokenLocation = TokenLocation.HEADER;
    /**
     * 取 token 的方式之 header
     */
    @NestedConfigurationProperty
    private final Header header = new Header();
    /**
     * token 存在 cookie 里
     */
    @NestedConfigurationProperty
    private final Cookie cookie = new Cookie();
    /**
     * 取 token 的方式之 parameter
     */
    @NestedConfigurationProperty
    private final Parameter parameter = new Parameter();
    /**
     * authorizerNames,多个以逗号分隔(不包含自己注入的 {@link Authorizer})
     * <p>
     * !!! 以下 {@link #excludePath} 和 {@link #excludeBranch} 和 {@link #excludeRegex} 排除掉的之外的地址都生效 !!! <p>
     * 参考 {@link DefaultAuthorizationChecker}
     */
    private String authorizerNames = DefaultAuthorizers.NONE;
    /**
     * matcherNames,多个以逗号分隔(不包含自己注入的 {@link Matcher})
     * <p>
     * !!! 全部地址都生效 !!! <p>
     * 参考 {@link DefaultMatchingChecker}
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
     * 登录页面的 url
     * <p>
     * 配置后会自动加入地址过滤链,避免请求该地址被拦截,
     * 并且前后端不分离下访问授权保护的页面未通过鉴权会被重定向到登录页
     */
    private String loginUrl;
    /**
     * 登出请求的 url
     * <p>
     * 请求该地址会自动调用 {@link SecurityManager#logout(TokenProfile)},
     * 前后端不分离下会重定向到 {@link #loginUrl}
     */
    private String logoutUrl;
    /**
     * 触发三方登录的url
     * <p>
     * 配置后此url会被拦截进行重定向到相应的网址进行三方登陆,
     * 前后端不分离下注入 {@link IndirectClient} 后才有效
     */
    private String sfLoginUrl;
    /**
     * callback url
     * <p>
     * 三方登录的回调地址,回调后触发 {@link CallbackHandler},
     * 前后端不分离下注入 {@link IndirectClient} 后才有效
     */
    private String callbackUrl;
    /**
     * 拦截模式
     */
    private InterceptModel model = InterceptModel.INTERCEPTOR;

    @Data
    public static class Annotations {
        /**
         * 是否启用注解拦截
         */
        private boolean enabled = true;
    }
}
