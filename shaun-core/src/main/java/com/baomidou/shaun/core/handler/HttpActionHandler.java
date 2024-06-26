/*
 * Copyright 2019-2024 baomidou (wonderming@vip.qq.com)
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
package com.baomidou.shaun.core.handler;

import com.baomidou.shaun.core.config.CoreConfig;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.HttpAction;

/**
 * HttpAction 处理器
 *
 * @author miemie
 * @since 2019-08-08
 */
public interface HttpActionHandler {

    /**
     * 处理拦截器发出的 HttpAction
     *
     * @param config  核心
     * @param context 上下文
     * @param action  异常
     */
    void handle(CoreConfig config, CallContext context, HttpAction action);
}
