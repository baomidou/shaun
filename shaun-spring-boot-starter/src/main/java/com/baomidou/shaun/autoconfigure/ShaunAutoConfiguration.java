package com.baomidou.shaun.autoconfigure;

import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.core.context.DefaultJ2EContextFactory;
import com.baomidou.shaun.core.context.J2EContextFactory;
import com.baomidou.shaun.core.context.session.NoSessionStore;
import com.baomidou.shaun.core.extractor.TokenExtractor;
import com.baomidou.shaun.core.generator.DefaultJwtTokenGenerator;
import com.baomidou.shaun.core.generator.TokenGenerator;
import com.baomidou.shaun.core.profile.ProfileManagerFactory;
import lombok.AllArgsConstructor;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author miemie
 * @since 2019-07-18
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(ShaunProperties.class)
public class ShaunAutoConfiguration {

    private final ShaunProperties properties;

    /**
     * session 操作类(默认不对session进行任何操作)
     */
    @Bean
    @ConditionalOnMissingBean
    public SessionStore sessionStore() {
        return NoSessionStore.INSTANCE;
    }

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
     * j2eContext 生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public J2EContextFactory j2EContextFactory() {
        return DefaultJ2EContextFactory.INSTANCE;
    }

    @Bean
    @ConditionalOnMissingBean
    public ProfileManagerFactory profileManagerFactory() {
        return ProfileManagerFactory.DEFAULT;
    }
}