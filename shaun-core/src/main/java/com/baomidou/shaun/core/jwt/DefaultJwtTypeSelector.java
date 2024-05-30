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
package com.baomidou.shaun.core.jwt;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
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
public class DefaultJwtTypeSelector extends InitializableObject implements JwtTypeSelector {

    private JwtAuthenticator jwtAuthenticator;
    private SignatureConfiguration signatureConfiguration;
    private EncryptionConfiguration encryptionConfiguration;

    /**
     * 不推荐
     */
    public DefaultJwtTypeSelector() {
        this(JwtType.NONE, null);
    }

    public DefaultJwtTypeSelector(JwtType type, String sign) {
        if (type != null && type != JwtType.NONE) {
            CommonHelper.assertNotBlank(sign, "sign");
            if (type == JwtType.DEFAULT) {
                this.signatureConfiguration = new SecretSignatureConfiguration(sign);
                this.encryptionConfiguration = new SecretEncryptionConfiguration(sign);
            } else if (type == JwtType.ONLY_ENCRYPTION) {
                this.encryptionConfiguration = new SecretEncryptionConfiguration(sign);
            } else if (type == JwtType.ONLY_SIGNATURE) {
                this.signatureConfiguration = new SecretSignatureConfiguration(sign);
            }
        }
    }

    public DefaultJwtTypeSelector(SignatureConfiguration signatureConfiguration) {
        this(signatureConfiguration, null);
    }

    public DefaultJwtTypeSelector(EncryptionConfiguration encryptionConfiguration) {
        this(null, encryptionConfiguration);
    }

    public DefaultJwtTypeSelector(SignatureConfiguration signatureConfiguration, EncryptionConfiguration encryptionConfiguration) {
        this.signatureConfiguration = signatureConfiguration;
        this.encryptionConfiguration = encryptionConfiguration;
    }

    @Override
    public JwtGenerator getGenerator() {
        init();
        return new ShaunJwtGenerator(signatureConfiguration, encryptionConfiguration);
    }

    @Override
    public JwtAuthenticator getAuthenticator() {
        init();
        return jwtAuthenticator;
    }

    @Override
    protected void internalInit(boolean forceReinit) {
        this.jwtAuthenticator = new JwtAuthenticator();
        if (this.signatureConfiguration != null) {
            jwtAuthenticator.setSignatureConfiguration(this.signatureConfiguration);
        }
        if (this.encryptionConfiguration != null) {
            jwtAuthenticator.setEncryptionConfiguration(this.encryptionConfiguration);
        }
    }
}
