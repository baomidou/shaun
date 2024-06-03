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
package com.baomidou.shaun.core.exception.http;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.http.RedirectionAction;

import java.io.Serial;

/**
 * 跳转登录页
 *
 * @author miemie
 * @since 2020-12-01
 */
public class FoundLoginAction extends RedirectionAction {
    @Serial
    private static final long serialVersionUID = 3129463209921232281L;
    public static final FoundLoginAction INSTANCE = new FoundLoginAction();

    protected FoundLoginAction() {
        super(HttpConstants.FOUND);
    }
}
