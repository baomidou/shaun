package com.baomidou.mipac4j.autoconfigure;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.context.cookie.CookieContext;
import com.baomidou.mipac4j.core.context.http.DefaultDoHttpAction;
import com.baomidou.mipac4j.core.context.http.DoHttpAction;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.generator.TokenGenerator;
import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-07-18
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(MIPac4jProperties.class)
public class MIPac4jAutoConfiguration {

    private final MIPac4jProperties properties;

    /**
     * jwt 签名类
     */
    @Bean
    @ConditionalOnMissingBean
    public SignatureConfiguration signatureConfiguration() {
        return new SecretSignatureConfiguration(properties.getSalt());
    }

    /**
     * jwt 加密类
     */
    @Bean
    @ConditionalOnMissingBean
    public EncryptionConfiguration encryptionConfiguration() {
        return new SecretEncryptionConfiguration(properties.getSalt());
    }

    /**
     * 操作 cookie 类
     */
    @Bean
    @ConditionalOnMissingBean
    public CookieContext cookieContext(J2EContextFactory j2EContextFactory, TokenGenerator tokenGenerator, SessionStore sessionStore) {
        return new CookieContext(j2EContextFactory, tokenGenerator, sessionStore, properties.getCookie());
    }

    @Bean
    @ConditionalOnMissingBean
    public DoHttpAction doHttpAction() {
        return new DefaultDoHttpAction();
    }

    /**
     * logout 执行器
     */
    @Bean
    @ConditionalOnMissingBean
    public LogoutExecutor logoutExecutor() {
        return LogoutExecutor.DO_NOTHING;
    }

    @Bean
    @ConditionalOnMissingBean
    public ProfileManagerFactory profileManagerFactory() {
        return ProfileManagerFactory.DEFAULT;
    }
}
