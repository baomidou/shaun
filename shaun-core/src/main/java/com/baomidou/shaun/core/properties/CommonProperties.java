package com.baomidou.shaun.core.properties;

import lombok.Data;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;

import java.util.List;

/**
 * @author miemie
 * @since 2019-07-30
 */
@Data
public class CommonProperties {

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
}
