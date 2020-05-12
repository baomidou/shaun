package com.baomidou.shaun.core.generator;

import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;

import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.ExpireTimeUtil;

import lombok.Data;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DefaultJwtTokenGenerator implements TokenGenerator {

    private final AuthorityManager authorityManager;
    private final SignatureConfiguration signatureConfiguration;
    private final EncryptionConfiguration encryptionConfiguration;

    /**
     * 默认超时时间
     */
    private String defaultExpireTime;

    @Override
    public String generate(final TokenProfile profile, final boolean isSkipAuthenticationUser, String expireTime) {
        if (isSkipAuthenticationUser) {
            authorityManager.skipAuthentication(profile);
        }
        JwtGenerator<TokenProfile> jwtGenerator = new JwtGenerator<>(signatureConfiguration, encryptionConfiguration);
        boolean defaultExpire = CommonHelper.isNotBlank(defaultExpireTime);
        boolean optionExpire = CommonHelper.isNotBlank(expireTime);
        if (defaultExpire || optionExpire) {
            if (!defaultExpire || optionExpire) {
                jwtGenerator.setExpirationTime(ExpireTimeUtil.getTargetDate(expireTime));
            } else {
                jwtGenerator.setExpirationTime(ExpireTimeUtil.getTargetDate(defaultExpireTime));
            }
        }
        return jwtGenerator.generate(profile);
    }

    /**
     * 默认提前1秒到期
     */
    @Override
    public int getAge(String optionExpireTime) {
        boolean defaultExpire = CommonHelper.isNotBlank(defaultExpireTime);
        boolean optionExpire = CommonHelper.isNotBlank(optionExpireTime);
        if (defaultExpire || optionExpire) {
            int expireTime;
            if (!defaultExpire || optionExpire) {
                expireTime = ExpireTimeUtil.getTargetSecond(optionExpireTime);
            } else {
                expireTime = ExpireTimeUtil.getTargetSecond(defaultExpireTime);
            }
            return expireTime - 1;
        }
        return -1;
    }

    public void setDefaultExpireTime(String defaultExpireTime) {
        this.defaultExpireTime = defaultExpireTime;
        ExpireTimeUtil.getTargetSecond(defaultExpireTime); // check 一下表达式是否正确
    }
}
