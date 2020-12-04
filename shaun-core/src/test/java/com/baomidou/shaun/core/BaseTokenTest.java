package com.baomidou.shaun.core;

import com.baomidou.shaun.core.jwt.DefaultJwtModelSelector;
import com.baomidou.shaun.core.jwt.JwtModelSelector;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;

import java.util.UUID;

/**
 * @author miemie
 * @since 2020-12-03
 */
public abstract class BaseTokenTest {

    private final static String SIGN = "9bfd771777984dc89868fd258f823e48";
    protected final SignatureConfiguration signatureConfiguration = new SecretSignatureConfiguration(SIGN);
    protected final EncryptionConfiguration encryptionConfiguration = new SecretEncryptionConfiguration(SIGN);
    protected final JwtModelSelector noneSelector = new DefaultJwtModelSelector();
    protected final JwtModelSelector signatureSelector = new DefaultJwtModelSelector(signatureConfiguration);
    protected final JwtModelSelector encryptionSelector = new DefaultJwtModelSelector(encryptionConfiguration);
    protected final JwtModelSelector bothSelector = new DefaultJwtModelSelector(signatureConfiguration, encryptionConfiguration);

    protected String str(int len) {
        StringBuilder s = new StringBuilder(uuid());
        while (s.length() < len) {
            s.append(uuid());
        }
        if (s.length() > len) {
            s.setLength(len);
        }
        return s.toString();
    }

    protected String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
