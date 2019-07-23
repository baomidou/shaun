package com.baomidou.ezpac4j.stateless.autoconfigure;

import org.pac4j.core.context.session.J2ESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.matching.PathMatcher;
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
import org.springframework.util.CollectionUtils;

import com.baomidou.pac4jplus.core.client.TokenClient;
import com.baomidou.pac4jplus.core.context.CookieContext;
import com.baomidou.pac4jplus.core.context.DefaultJ2EContextFactory;
import com.baomidou.pac4jplus.core.context.J2EContextFactory;
import com.baomidou.pac4jplus.core.engine.LogoutExecutor;
import com.baomidou.pac4jplus.core.extractor.TokenExtractor;
import com.baomidou.pac4jplus.core.generator.DefaultJwtTokenGenerator;
import com.baomidou.pac4jplus.core.generator.TokenGenerator;
import com.baomidou.pac4jplus.properties.Pac4jProperties;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-07-18
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(Pac4jProperties.class)
public class Pac4jPlusAutoConfiguration {

    private final Pac4jProperties properties;

    /**
     * session 存储器,主要存储 profiles,但我们不用 session,注入这个 bean 只是为了 pac4j 内部少 new 这个类
     */
    @Bean
    @ConditionalOnMissingBean
    public SessionStore sessionStore() {
        return new J2ESessionStore();
    }

    /**
     * 验证是否该拦截该 request 的匹配器
     */
    @Bean
    @ConditionalOnMissingBean
    public Matcher matcher() {
        PathMatcher pathMatcher = new PathMatcher();
        if (!CollectionUtils.isEmpty(properties.getExcludePath())) {
            properties.getExcludePath().forEach(pathMatcher::excludePath);
        }
        if (!CollectionUtils.isEmpty(properties.getExcludeBranch())) {
            properties.getExcludeBranch().forEach(pathMatcher::excludeBranch);
        }
        if (!CollectionUtils.isEmpty(properties.getExcludeRegex())) {
            properties.getExcludeBranch().forEach(pathMatcher::excludeRegex);
        }
        return pathMatcher;
    }

    /**
     * 检索 token 并验证
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenClient tokenClient(JwtAuthenticator jwtAuthenticator, TokenExtractor tokenExtractor) {
        return new TokenClient(tokenExtractor, jwtAuthenticator);
    }

    /**
     * 定义了从 WebContext 取 token 的方式
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenExtractor tokenExtractor() {
        return new TokenExtractor(properties.getTokenType(), properties.getHeader(), properties.getParameter(), properties.getCookie());
    }

    /**
     * 构建 J2EContext 的工厂类
     */
    @Bean
    @ConditionalOnMissingBean
    public J2EContextFactory j2EContextFactory() {
        return new DefaultJ2EContextFactory();
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
     * token 的验证器
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticator jwtAuthenticator(SignatureConfiguration signatureConfiguration, EncryptionConfiguration encryptionConfiguration) {
        return new JwtAuthenticator(signatureConfiguration, encryptionConfiguration);
    }

    /**
     * token 制造器
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenGenerator tokenGenerator(SignatureConfiguration signatureConfiguration, EncryptionConfiguration encryptionConfiguration) {
        return new DefaultJwtTokenGenerator(signatureConfiguration, encryptionConfiguration).setExpireTime(properties.getExpireTime());
    }

    /**
     * 操作 cookie 类
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "pac4j", name = "token-type", havingValue = "cookie")
    public CookieContext cookieContext(J2EContextFactory j2EContextFactory, TokenGenerator tokenGenerator, SessionStore sessionStore) {
        return new CookieContext(j2EContextFactory, tokenGenerator, sessionStore, properties.getCookie());
    }

    /**
     * logout 执行器
     */
    @Bean
    @ConditionalOnMissingBean
    public LogoutExecutor logoutExecutor() {
        return LogoutExecutor.DO_NOTHING;
    }
}
