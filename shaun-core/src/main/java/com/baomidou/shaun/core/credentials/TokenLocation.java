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
package com.baomidou.shaun.core.credentials;

/**
 * token 存放位置
 *
 * @author miemie
 * @since 2019-07-20
 */
public enum TokenLocation {
    /**
     * 请求头
     */
    HEADER,
    /**
     * cookie
     */
    COOKIE,
    /**
     * 请求的 parameter
     */
    PARAMETER,
    /**
     * 请求头 和 cookie
     */
    HEADER_OR_COOKIE,
    /**
     * 请求头 和 请求的 parameter
     */
    HEADER_OR_PARAMETER,
    /**
     * 请求头 和 cookie 和 请求的 parameter
     */
    HEADER_OR_COOKIE_OR_PARAMETER;

    public boolean enableCookie() {
        return this == COOKIE || this == HEADER_OR_COOKIE || this == HEADER_OR_COOKIE_OR_PARAMETER;
    }
}
