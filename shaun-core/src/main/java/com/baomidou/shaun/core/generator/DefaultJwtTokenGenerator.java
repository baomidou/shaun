package com.baomidou.shaun.core.generator;

import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;

import com.baomidou.shaun.core.authorizer.admin.AdminAuthorizer;
import com.baomidou.shaun.core.util.ExpireTimeUtil;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 默认使用 pac4j 的 JwtGenerator 生成 token(jwt)
 * 如果不使用默认的,还需要自己实现 {@link Authenticator}<{@link TokenCredentials}>
 * 并在 validate 后把 userprofile 放进 <{@link TokenCredentials}> 里
 *
 * @author miemie
 * @since 2019-07-18
 */
@Data
@Accessors(chain = true)
public class DefaultJwtTokenGenerator implements TokenGenerator {

    private final AdminAuthorizer adminAuthorizer;
    private final SignatureConfiguration signatureConfiguration;
    private final EncryptionConfiguration encryptionConfiguration;

    public DefaultJwtTokenGenerator(AdminAuthorizer adminAuthorizer, SignatureConfiguration signatureConfiguration,
                                    EncryptionConfiguration encryptionConfiguration) {
        this.adminAuthorizer = adminAuthorizer;
        this.signatureConfiguration = signatureConfiguration;
        this.encryptionConfiguration = encryptionConfiguration;
    }

    /**
     * jwt 超时时间
     */
    private String expireTime;

    @Override
    public <U extends CommonProfile> String generate(final U profile, final boolean isAdmin) {
        if (isAdmin) {
            adminAuthorizer.setAdmin(profile);
        }
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
            return ExpireTimeUtil.getTargetSecond(expireTime) - 1;
        }
        return null;
    }
}
