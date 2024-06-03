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
package com.baomidou.shaun.core.util;

import org.pac4j.core.exception.http.BadRequestAction;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.UnauthorizedAction;

/**
 * @author miemie
 * @since 2024/5/30
 */
public class HttpActionInstance {
    /**
     * 400
     */
    public static final BadRequestAction BAD_REQUEST = new BadRequestAction();
    /**
     * 401
     */
    public static final UnauthorizedAction UNAUTHORIZED = new UnauthorizedAction();
    /**
     * 403
     */
    public static final ForbiddenAction FORBIDDEN = new ForbiddenAction();
}
