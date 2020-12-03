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

    @Override
    public JwtGenerator<TokenProfile> getJwtGenerator() {
        return new JwtGenerator<>(signatureConfiguration, encryptionConfiguration);
    }

    @Override
    public JwtAuthenticator getJwtAuthenticator() {
        return jwtAuthenticator;
    }
}
