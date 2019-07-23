package com.baomidou.mipac4j.core.core.generator;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;

import com.baomidou.mipac4j.core.enums.ExpireType;
import com.baomidou.mipac4j.core.util.ExpireTimeUtil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

/**
 * @author miemie
 * @since 2019-07-18
 */
@Data
@AllArgsConstructor
public class DefaultJwtTokenGenerator implements TokenGenerator {

    private SignatureConfiguration signatureConfiguration;

    private EncryptionConfiguration encryptionConfiguration;

    /**
     * jwt 超时时间
     */
    private String expireTime;
    /**
     * 类型
     */
    @Setter(AccessLevel.NONE)
    private ExpireType expireType;

    public DefaultJwtTokenGenerator(SignatureConfiguration signatureConfiguration, EncryptionConfiguration encryptionConfiguration) {
        this.signatureConfiguration = signatureConfiguration;
        this.encryptionConfiguration = encryptionConfiguration;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends CommonProfile> String generate(final U profile) {
        JwtGenerator jwtGenerator = new JwtGenerator(signatureConfiguration, encryptionConfiguration);
        if (expireType != null) {
            jwtGenerator.setExpirationTime(ExpireTimeUtil.getTargetDate(expireType, expireTime));
        }
        return jwtGenerator.generate(profile);
    }

    /**
     * 默认提前1秒到期
     */
    @Override
    public Integer getAge() {
        if (expireType != null) {
            return ExpireTimeUtil.getExpireTime(expireType, expireTime) - 1;
        }
        return null;
    }

    public DefaultJwtTokenGenerator setExpireTime(String expireTime) {
        this.expireTime = expireTime;
        this.expireType = ExpireType.chooseType(expireTime);
        return this;
    }
}
