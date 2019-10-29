package com.baomidou.shaun.autoconfigure.properties;

import java.util.List;
import java.util.UUID;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.baomidou.shaun.core.enums.Model;
import com.baomidou.shaun.core.enums.TokenLocation;
import com.baomidou.shaun.core.properties.Cookie;
import com.baomidou.shaun.core.properties.Header;
import com.baomidou.shaun.core.properties.Parameter;

import lombok.Data;

/**
 * @author miemie
 * @since 2019-07-01
 */
@Data
@ConfigurationProperties("shaun")
public class ShaunProperties {

    /**
     * 模式
     */
    private Model model = Model.INTERCEPTOR;
    /**
     * authorizers,多个以逗号分隔(不包含自己注入的 {@link Authorizer})
     * 默认支持的一些参考 {@link DefaultAuthorizationChecker}
     */
    private String authorizers = "";
    /**
     * 管理员的 role 和 permission 的表现字符串
     */
    private String adminRolePermission = "shaun-admin-role-permission";
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
     */
    private String expireTime;
    /**
     * token 的存放位置
     * 前后不分离下,只支持 cookie ,且必须手动设置为 cookie
     */
    private TokenLocation tokenLocation = TokenLocation.HEADER;
    /**
     * 取 token 的方式之 header
     */
    @NestedConfigurationProperty
    private final Header header = new Header();
    /**
     * 取 token 的方式之 cookie
     */
    @NestedConfigurationProperty
    private final Cookie cookie = new Cookie();
    /**
     * 取 token 的方式之 parameter
     */
    @NestedConfigurationProperty
    private final Parameter parameter = new Parameter();
    /**
     * 登出 url
     */
    private String logoutUrl;

    /**
     * 登录页面 url,非分离模式下进行 redirect
     */
    private String loginUrl;
    /**
     * 触发三方登录的url
     */
    private String sfLoginUrl;
    /**
     * callback url
     * 三分登录的回调地址
     */
    private String callbackUrl;
    /**
     * index url
     * 三分登录的回调成功后 redirect 的主页
     */
    private String indexUrl;
}
