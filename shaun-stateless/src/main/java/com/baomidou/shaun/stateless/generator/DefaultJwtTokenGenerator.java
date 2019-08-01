package com.baomidou.shaun.stateless.generator;

import com.baomidou.shaun.core.util.ExpireTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;

/**
 * @author miemie
 * @since 2019-07-18
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class DefaultJwtTokenGenerator implements TokenGenerator {

    private SignatureConfiguration signatureConfiguration;

    private EncryptionConfiguration encryptionConfiguration;

    /**
     * jwt 超时时间
     */
    private Integer expireTime;

    public DefaultJwtTokenGenerator(SignatureConfiguration signatureConfiguration, EncryptionConfiguration encryptionConfiguration) {
        this.signatureConfiguration = signatureConfiguration;
        this.encryptionConfiguration = encryptionConfiguration;
    }

    @Override
    public <U extends CommonProfile> String generate(final U profile) {
        JwtGenerator<U> jwtGenerator = new JwtGenerator<>(signatureConfiguration, encryptionConfiguration);
        if (expireTime != null) {
            jwtGenerator.setExpirationTime(ExpireTimeUtil.getTargetDate(expireTime));
        }
        return jwtGenerator.generate(profile);
    }

    /**
     * 默认提前1秒到期
     */
    @Override
    public Integer getAge() {
        if (expireTime != null) {
            return expireTime - 1;
        }
        return null;
    }
}
