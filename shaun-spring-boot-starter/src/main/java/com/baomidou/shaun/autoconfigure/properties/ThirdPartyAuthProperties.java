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

import com.baomidou.shaun.core.handler.CallbackHandler;
import lombok.Data;
import org.pac4j.core.client.IndirectClient;

/**
 * @author miemie
 * @since 2022-06-06
 */
@Data
public class ThirdPartyAuthProperties {

    /**
     * 是否启用
     */
    private boolean enable = true;
    /**
     * 触发三方登录的 path
     * <p>
     * 配置后此url会被拦截进行重定向到相应的网址进行三方登陆,
     * 前后端不分离下注入 {@link IndirectClient} 后才有效
     */
    private String triggerPath;
    /**
     * callback path
     * <p>
     * 三方登录的回调地址,回调后触发 {@link CallbackHandler},
     * 前后端不分离下注入 {@link IndirectClient} 后才有效
     */
    private String callbackPath;
}
