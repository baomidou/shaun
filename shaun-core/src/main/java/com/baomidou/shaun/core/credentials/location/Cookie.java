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
package com.baomidou.shaun.core.credentials.location;

import lombok.Data;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.http.credentials.extractor.CookieExtractor;

/**
 * {@link CookieExtractor}
 * {@link org.pac4j.core.context.Cookie}
 *
 * @author miemie
 * @since 2019-07-20
 */
@Data
public class Cookie {

    private String name = Pac4jConstants.SESSION_ID;
    private int version = 0;
    private String comment;
    private String domain = "";
    private String path = Pac4jConstants.DEFAULT_URL_VALUE;
    private boolean secure;
    private boolean isHttpOnly;

    /**
     * 获取pac4j的cookie
     *
     * @param token  token
     * @param maxAge 存活时间
     * @return cookie
     */
    public org.pac4j.core.context.Cookie getPac4jCookie(final String token, int maxAge) {
        org.pac4j.core.context.Cookie cookie = new org.pac4j.core.context.Cookie(name, token);
        cookie.setVersion(version);
        cookie.setSecure(secure);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setComment(comment);
        cookie.setDomain(domain);
        return cookie;
    }
}
