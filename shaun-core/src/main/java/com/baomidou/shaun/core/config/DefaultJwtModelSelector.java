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
package com.baomidou.shaun.core.config;

import com.baomidou.shaun.core.profile.TokenProfile;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;

/**
 * @author miemie
 * @since 2020-12-03
 */
public class DefaultJwtModelSelector implements JwtModelSelector {

    private final JwtAuthenticator jwtAuthenticator;
    private SignatureConfiguration signatureConfiguration;
    private EncryptionConfiguration encryptionConfiguration;

    public DefaultJwtModelSelector(JwtModel model, String sign) {
        if (model != JwtModel.NONE) {
            if (model == JwtModel.SIGNATURE_ENCRYPTION) {
                this.signatureConfiguration = new SecretSignatureConfiguration(sign);
                this.encryptionConfiguration = new SecretEncryptionConfiguration(sign);
            }
            if (model == JwtModel.ONLY_ENCRYPTION) {
                this.encryptionConfiguration = new SecretEncryptionConfiguration(sign);
            }
            if (model == JwtModel.ONLY_SIGNATURE) {
                this.signatureConfiguration = new SecretSignatureConfiguration(sign);
            }
        }
        jwtAuthenticator = new JwtAuthenticator();
        if (this.signatureConfiguration != null) {
            jwtAuthenticator.setSignatureConfiguration(this.signatureConfiguration);
        }
        if (this.encryptionConfiguration != null) {
            jwtAuthenticator.setEncryptionConfiguration(this.encryptionConfiguration);
        }
    }

    public DefaultJwtModelSelector(SignatureConfiguration signatureConfiguration, EncryptionConfiguration encryptionConfiguration) {
        this.signatureConfiguration = signatureConfiguration;
        this.encryptionConfiguration = encryptionConfiguration;
        jwtAuthenticator = new JwtAuthenticator();
        if (this.signatureConfiguration != null) {
            jwtAuthenticator.setSignatureConfiguration(this.signatureConfiguration);
        }
        if (this.encryptionConfiguration != null) {
            jwtAuthenticator.setEncryptionConfiguration(this.encryptionConfiguration);
        }
    }

    @Override
    public JwtGenerator<TokenProfile> getJwtGenerator() {
        return new JwtGenerator<>(signatureConfiguration, encryptionConfiguration);
    }

    @Override
    public JwtAuthenticator getJwtAuthenticator() {
        return jwtAuthenticator;
    }
}