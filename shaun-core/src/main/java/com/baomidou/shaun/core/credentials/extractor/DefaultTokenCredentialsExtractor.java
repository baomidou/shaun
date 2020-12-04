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
package com.baomidou.shaun.core.credentials.extractor;

import com.baomidou.shaun.core.credentials.TokenLocation;
import com.baomidou.shaun.core.credentials.location.Cookie;
import com.baomidou.shaun.core.credentials.location.Header;
import com.baomidou.shaun.core.credentials.location.Parameter;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.HeaderExtractor;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.http.credentials.extractor.CookieExtractor;

import java.util.Optional;

/**
 * @author miemie
 * @since 2019-07-20
 */
public class DefaultTokenCredentialsExtractor implements TokenCredentialsExtractor {

    private final TokenLocation tokenLocation;
    private final HeaderExtractor headerExtractor;
    private final CookieExtractor cookieExtractor;
    private final ParameterExtractor parameterExtractor;

    public DefaultTokenCredentialsExtractor(TokenLocation tokenLocation, Header header, Cookie cookie, Parameter parameter) {
        this.tokenLocation = tokenLocation;
        this.headerExtractor = new HeaderExtractor(header.getName(), header.getPrefix());
        this.headerExtractor.setTrimValue(header.isTrimValue());
        this.cookieExtractor = new CookieExtractor(cookie.getName());
        this.parameterExtractor = new ParameterExtractor(parameter.getName(),
                parameter.isSupportGetRequest(), parameter.isSupportPostRequest());
    }

    @Override
    public Optional<TokenCredentials> extract(WebContext context) {
        Optional<TokenCredentials> credentials = Optional.empty();
        switch (tokenLocation) {
            case HEADER:
                credentials = headerExtractor.extract(context);
                break;
            case COOKIE:
                credentials = cookieExtractor.extract(context);
                break;
            case PARAMETER:
                credentials = parameterExtractor.extract(context);
                break;
            case HEADER_OR_COOKIE:
                credentials = headerExtractor.extract(context);
                if (!credentials.isPresent()) {
                    credentials = cookieExtractor.extract(context);
                }
                break;
            case HEADER_OR_PARAMETER:
                credentials = headerExtractor.extract(context);
                if (!credentials.isPresent()) {
                    credentials = parameterExtractor.extract(context);
                }
                break;
            case HEADER_OR_COOKIE_OR_PARAMETER:
                credentials = headerExtractor.extract(context);
                if (!credentials.isPresent()) {
                    credentials = cookieExtractor.extract(context);
                }
                if (!credentials.isPresent()) {
                    credentials = parameterExtractor.extract(context);
                }
                break;
        }
        return credentials;
    }
}
