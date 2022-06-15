/*
 * Copyright 2019-2022 baomidou (wonderming@vip.qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.autoconfigure.properties;

import java.util.List;
import java.util.UUID;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.baomidou.shaun.core.credentials.TokenLocation;
import com.baomidou.shaun.core.credentials.location.Cookie;
import com.baomidou.shaun.core.credentials.location.Header;
import com.baomidou.shaun.core.credentials.location.Parameter;
import com.baomidou.shaun.core.jwt.JwtType;
import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.profile.TokenProfile;

import lombok.Data;

/**
 * @author miemie
 * @since 2022-06-06
 */
@Data
public class SecurityProperties {

    public static final String DEFAULT_SKIP_AUTHENTICATION_ROLE_PERMISSION = "shaun-admin-role-permission";

    /**
     * 是否启用
     */
    private boolean enable = true;
    /**
     * jwt 的配置
     */
    private final Jwt jwt = new Jwt();
    /**
     * 提取 jwt 配置
     */
    private final Extractor extractor = new Extractor();
    /**
     * 排除路径配置
     */
    private final ExcludePath excludePath = new ExcludePath();
    /**
     * 是否启用: 验证通过注解指定用户拥有的 role 和 permission 的权限
     */
    private boolean annotation = true;
    /**
     * 登出请求的 url
     * <p>
     * 请求该地址会自动调用 {@link SecurityManager#logout(TokenProfile)},
     * 前后端不分离下会重定向到 loginUrl
     */
    private String logoutUrl;
    /**
     * 跳过鉴权的 role 和 permission 的表现字符串(相当于系统超管)
     */
    private String skipAuthenticationRolePermission = DEFAULT_SKIP_AUTHENTICATION_ROLE_PERMISSION;
    /**
     * authorizerNames,多个以逗号分隔(不包含自己注入的 {@link Authorizer})
     * <p>
     * !!! 需要用户有登录信息的地址生效,比 matcher 晚 !!! <p>
     * 参考 {@link DefaultAuthorizationChecker}
     */
    private String authorizerNames = DefaultAuthorizers.NONE;

    @Data
    public static class Jwt {

        /**
         * jwt 模式
         */
        private JwtType type = JwtType.DEFAULT;
        /**
         * jwt 盐值(默认只支持 32 位字符)
         */
        private String salt = UUID.randomUUID().toString().replace("-", "");
        /**
         * jwt 超时时间: <br>
         * 10s : 表示10秒有效
         * 10m 结尾: 表示10分钟有效
         * 10h 结尾: 表示10小时有效
         * <p>
         * 1d : 表示有效时间到第二天 00:00:00
         * 2d1h : 表示有效时间到第三天 01:00:00
         * `d` 后面 只支持上面三个(`s`,`m`,`h`)之一
         *
         * <p>
         * 纯 cookie 模式下可以不设置,则cookie过期时间为会话时间但是token永不过期
         * </p>
         */
        private String expireTime;
    }

    @Data
    public static class Extractor {

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
         * token 的存放位置
         */
        private TokenLocation location = TokenLocation.HEADER;
    }

    @Data
    public static class ExcludePath {

        /**
         * 排除的 url
         */
        private List<String> path;
        /**
         * 排除的 url 的统一前缀
         */
        private List<String> branch;
        /**
         * 排除的 url 的 正则表达式
         */
        private List<String> regex;
    }
}
