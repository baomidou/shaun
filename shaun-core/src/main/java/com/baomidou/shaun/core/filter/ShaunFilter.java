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
package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.CoreConfig;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.jee.context.JEEContext;

/**
 * 内部 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
public interface ShaunFilter {

    /**
     * @param config  全局配置
     * @param context webContext
     * @return HttpAction
     */
    HttpAction doFilter(CoreConfig config, JEEContext context);

    /**
     * 有多个子类时执行顺序(越小越优先)
     * <p>
     * 默认一些优先级:
     * ThirdPartyCallbackFilter: 0
     * ThirdPartyAuthLoginFilter: 100
     * SecurityFilter: 200
     * LogoutFilter: 300
     * ActuatorFilter: 400
     *
     * @return int
     */
    default int order() {
        return 0;
    }

    default void initCheck() {
        // ignore
    }
}
