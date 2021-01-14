/*
 * Copyright 2019-2021 baomidou (wonderming@vip.qq.com)
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
package com.baomidou.shaun.core.client;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;

/**
 * 检索 token 并验证
 *
 * @author miemie
 * @since 2019-07-20
 */
public class TokenClient extends DirectClient<TokenCredentials> {

    public TokenClient(final CredentialsExtractor<TokenCredentials> credentialsExtractor,
                       final JwtAuthenticator tokenAuthenticator) {
        defaultCredentialsExtractor(credentialsExtractor);
        defaultAuthenticator(tokenAuthenticator);
    }

    @Override
    protected void clientInit() {
        // ignore
    }
}
