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
package com.baomidou.shaun.core.credentials.location;

import lombok.Data;
import org.pac4j.core.context.CallContext;
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
    private String domain = "";
    private String path = Pac4jConstants.DEFAULT_URL_VALUE;
    private boolean secure;
    private boolean isHttpOnly = false;
    private String sameSitePolicy;
    private String comment;

    /**
     * 获取pac4j的cookie
     *
     * @param token  token
     * @param maxAge 存活时间
     * @return cookie
     */
    public org.pac4j.core.context.Cookie genCookie(final String token, int maxAge) {
        org.pac4j.core.context.Cookie target = new org.pac4j.core.context.Cookie(name, token);
        target.setDomain(domain);
        target.setMaxAge(maxAge);
        target.setPath(path);
        target.setSecure(secure);
        target.setHttpOnly(isHttpOnly);
        target.setSameSitePolicy(sameSitePolicy);
        target.setComment(comment);
        return target;
    }

    /**
     * 清理 cookie
     */
    public void clean(CallContext context) {
        context.webContext().addResponseCookie(genCookie("", 0));
    }
}
