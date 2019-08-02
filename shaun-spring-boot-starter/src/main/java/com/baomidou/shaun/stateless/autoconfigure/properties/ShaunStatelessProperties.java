package com.baomidou.shaun.stateless.autoconfigure.properties;

import java.util.List;
import java.util.UUID;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
public class ShaunStatelessProperties {

    /**
     * 登出 url
     */
    private String logoutUrl;
    /**
     * authorizers,多个以逗号分隔(不包含自己注入的 {@link Authorizer})
     * 默认支持的一些参考 {@link DefaultAuthorizationChecker}
     */
    private String authorizers = "";
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
     * jwt 超时时间(单位秒)
     */
    private Integer expireTime;
    /**
     * token 的存放位置
     */
    private TokenLocation tokenLocation = TokenLocation.HEADER;
    /**
     * 取 token 的方式之 header
     */
    private Header header = new Header();
    /**
     * 取 token 的方式之 cookie
     */
    private Cookie cookie = new Cookie();
    /**
     * 取 token 的方式之 parameter
     */
    private Parameter parameter = new Parameter();
}
