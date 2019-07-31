package com.baomidou.shaun.stateless.autoconfigure;

import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.shaun.core.context.DefaultJ2EContextFactory;
import com.baomidou.shaun.core.context.J2EContextFactory;
import com.baomidou.shaun.core.context.cookie.CookieContext;
import com.baomidou.shaun.core.extractor.TokenExtractor;
import com.baomidou.shaun.core.generator.DefaultJwtTokenGenerator;
import com.baomidou.shaun.core.generator.TokenGenerator;
import com.baomidou.shaun.core.handler.logout.CookieLogoutHandler;
import com.baomidou.shaun.core.handler.logout.LogoutHandler;
import com.baomidou.shaun.stateless.autoconfigure.properties.ShaunStatelessProperties;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-07-18
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(ShaunStatelessProperties.class)
public class ShaunStatelessAutoConfiguration {

    private final ShaunStatelessProperties properties;

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
     * token 取的方式
     */
    @Bean
    @ConditionalOnMissingBean
    public CredentialsExtractor<TokenCredentials> credentialsExtractor() {
        return new TokenExtractor(properties.getTokenLocation(), properties.getHeader(), properties.getParameter(), properties.getCookie());
    }

    /**
     * token 检验器
     */
    @Bean
    @ConditionalOnMissingBean
    public Authenticator<TokenCredentials> authenticator(SignatureConfiguration signatureConfiguration,
                                                         EncryptionConfiguration encryptionConfiguration) {
        return new JwtAuthenticator(signatureConfiguration, encryptionConfiguration);
    }

    /**
     * token 生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenGenerator tokenGenerator(SignatureConfiguration signatureConfiguration,
                                         EncryptionConfiguration encryptionConfiguration) {
        return new DefaultJwtTokenGenerator(signatureConfiguration, encryptionConfiguration)
                .setExpireTime(properties.getExpireTime());
    }

    /**
     * 操作 cookie 类
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "shaun", name = "token-location", havingValue = "cookie")
    public CookieContext cookieContext(J2EContextFactory j2EContextFactory, TokenGenerator tokenGenerator) {
        return new CookieContext(j2EContextFactory, tokenGenerator, properties.getCookie());
    }

    /**
     * 默认 cookie 模式下的登出操作类
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "shaun", name = "token-location", havingValue = "cookie")
    public LogoutHandler<CommonProfile> logoutHandler(CookieContext cookieContext) {
        return new CookieLogoutHandler(cookieContext);
    }

    /**
     * 生成 J2EContext 的工厂类
     */
    @Bean
    @ConditionalOnMissingBean
    public J2EContextFactory j2EContextFactory() {
        return new DefaultJ2EContextFactory();
    }
}