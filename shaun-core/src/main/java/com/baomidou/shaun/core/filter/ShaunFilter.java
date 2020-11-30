/**
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.CoreConfig;
import org.pac4j.core.context.JEEContext;

/**
 * 内部 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
public interface ShaunFilter {

    /**
     * 是否继续执行 FilterChain.doFilter(request, response);
     *
     * @param config  全局配置
     * @param context webContext
     * @return 是否继续执行
     */
    boolean goOnChain(CoreConfig config, JEEContext context);

    /**
     * 有多个子类时执行顺序(越小越优先)
     *
     * @return int
     */
    default int order() {
        return 0;
    }

    void initCheck();
}
