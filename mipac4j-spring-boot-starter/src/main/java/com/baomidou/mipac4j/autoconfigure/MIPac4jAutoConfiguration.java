package com.baomidou.mipac4j.autoconfigure;

import org.pac4j.core.client.Client;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.http.credentials.extractor.CookieExtractor;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.client.TokenDirectClient;
import com.baomidou.mipac4j.core.client.TokenIndirectClient;
import com.baomidou.mipac4j.core.context.DefaultJ2EContextFactory;
import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.context.cookie.CookieContext;
import com.baomidou.mipac4j.core.context.http.DefaultDoHttpAction;
import com.baomidou.mipac4j.core.context.http.DoHttpAction;
import com.baomidou.mipac4j.core.context.session.NoSessionStore;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.extractor.TokenExtractor;
import com.baomidou.mipac4j.core.generator.DefaultJwtTokenGenerator;
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
     * session 存储器,选择不进行 session 存储
     */
    @Bean
    @ConditionalOnMissingBean
    public SessionStore sessionStore() {
        return new NoSessionStore();
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
     * 检索 token 并验证(前后台分离下)
     */
    @Bean
    @ConditionalOnMissingBean
    public Client tokenClient(CredentialsExtractor<TokenCredentials> credentialsExtractor, Authenticator<TokenCredentials> authenticator) {
        if (properties.isStateless()) {
            return new TokenDirectClient(credentialsExtractor, authenticator);
        }
        return new TokenIndirectClient(properties.getLoginUrl(), credentialsExtractor, authenticator);
    }

    /**
     * 定义了从 WebContext 取 token 的方式
     */
    @Bean
    @ConditionalOnMissingBean
    public CredentialsExtractor<TokenCredentials> tokenExtractor() {
        if (properties.isStateless()) {
            return new TokenExtractor(properties.getTokenLocation(), properties.getHeader(), properties.getParameter(), properties.getCookie());
        }
        return new CookieExtractor(properties.getCookie().getName());
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
    public Authenticator<TokenCredentials> jwtAuthenticator(SignatureConfiguration signatureConfiguration, EncryptionConfiguration encryptionConfiguration) {
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
