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

import com.baomidou.shaun.core.intercept.InterceptModel;
import lombok.Data;
import org.pac4j.core.matching.checker.DefaultMatchingChecker;
import org.pac4j.core.matching.matcher.DefaultMatchers;
import org.pac4j.core.matching.matcher.Matcher;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author miemie
 * @since 2020-11-09
 */
@Data
@ConfigurationProperties("shaun")
public class ShaunProperties {
    /**
     * 第三方验证登录的配置
     */
    @NestedConfigurationProperty
    private final ThirdPartyAuthProperties thirdParty = new ThirdPartyAuthProperties();
    /**
     * 是否-前后端分离
     */
    private boolean stateless = true;
    /**
     * 是否-启用session
     */
    private boolean sessionOn = false;
    /**
     * matcherNames,多个以逗号分隔(不包含自己注入的 {@link Matcher})
     * <p>
     * !!! 全局地址生效,早于所有其他拦截器 !!! <p>
     * 参考 {@link DefaultMatchingChecker}
     */
    private String matcherNames = DefaultMatchers.NONE;
    /**
     * 拦截模式
     */
    private InterceptModel model = InterceptModel.INTERCEPTOR;
    /**
     * 集中管理安全拦截地址
     */
    @NestedConfigurationProperty
    private final SecurityProperties security = new SecurityProperties();
    /**
     * spring actuator
     * <p>
     * 如果检测到项目里有 spring-boot-starter-actuator 该配置自动生效
     */
    @NestedConfigurationProperty
    private final ActuatorProperties actuator = new ActuatorProperties();
    /**
     * 登录页面的 url
     * <p>
     * 配置后会自动加入地址过滤链,避免请求该地址被拦截,
     * 并且前后端不分离下访问授权保护的页面未通过鉴权会被重定向到登录页
     */
    private String loginPath;
}
