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
package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.jwt.JwtTypeSelector;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.ExpireTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jwt.profile.JwtGenerator;

import java.util.Optional;

/**
 * @author miemie
 * @since 2020-10-29
 */
@Slf4j
public class JwtProfileTokenManager implements ProfileTokenManager {

    private final TokenClient tokenClient;
    private final JwtTypeSelector jwtTypeSelector;

    public JwtProfileTokenManager(JwtTypeSelector jwtTypeSelector, CredentialsExtractor credentialsExtractor) {
        this.jwtTypeSelector = jwtTypeSelector;
        this.tokenClient = new TokenClient(credentialsExtractor, jwtTypeSelector.getAuthenticator());
    }

    @Override
    public TokenProfile getProfile(CallContext context) {
        Credentials credentials = tokenClient.getCredentials(context).orElse(null);
        credentials = tokenClient.validateCredentials(context, credentials).orElse(null);
        Optional<UserProfile> profile = tokenClient.getUserProfile(context, credentials);
        TokenProfile tokenProfile = null;
        if (profile.isPresent()) {
            tokenProfile = (TokenProfile) profile.get();
            tokenProfile.setToken(((TokenCredentials) credentials).getToken());
            return tokenProfile;
        }
        return tokenProfile;
    }

    @Override
    public String generateToken(TokenProfile profile, String expireTime) {
        JwtGenerator jwtGenerator = jwtTypeSelector.getGenerator();
        if (CommonHelper.isNotBlank(expireTime)) {
            jwtGenerator.setExpirationTime(ExpireTimeUtil.getTargetDate(expireTime));
        }
        String jwt = jwtGenerator.generate(profile);
        final int length = jwt.length();
        if (length > 3072) {
            log.warn("the JWT length is {}, it's over 3072, please be careful!", length);
        }
        return jwt;
    }
}
