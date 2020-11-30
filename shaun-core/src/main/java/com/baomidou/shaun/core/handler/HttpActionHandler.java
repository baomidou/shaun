/*
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
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

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.BadRequestAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.UnauthorizedAction;

/**
 * 只能处理拦截到request的地方,
 * 主要用于处理 ajax 请求 ,
 * 不能处理权限注解产生的异常
 *
 * @author miemie
 * @since 2019-08-08
 */
public interface HttpActionHandler {

    /**
     * 处理抛出的异常 {@link UnauthorizedAction} 和 {@link BadRequestAction}
     *
     * @param action  异常
     * @param context 上下文
     */
    void preHandle(HttpAction action, JEEContext context);
}
