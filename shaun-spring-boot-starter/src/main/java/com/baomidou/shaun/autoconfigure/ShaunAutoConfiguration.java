package com.baomidou.shaun.autoconfigure;

import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.core.authorizer.AuthorizationProfile;
import com.baomidou.shaun.core.authorizer.DefaultAuthorizationProfile;
import com.baomidou.shaun.core.authorizer.admin.AdminAuthorizer;
import com.baomidou.shaun.core.authorizer.admin.DefaultAdminAuthorizer;
import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.extractor.TokenExtractor;
import com.baomidou.shaun.core.generator.DefaultJwtTokenGenerator;
import com.baomidou.shaun.core.generator.TokenGenerator;
import com.baomidou.shaun.core.handler.DefaultHttpActionHandler;
import com.baomidou.shaun.core.handler.DefaultLogoutHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.mgt.SecurityManager;

import lombok.AllArgsConstructor;

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
     * client
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenClient tokenClient(CredentialsExtractor<TokenCredentials> credentialsExtractor,
                                   Authenticator<TokenCredentials> authenticator) {
        return new TokenClient(credentialsExtractor, authenticator);
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
     * 判断以及设置 profile 为管理员
     */
    @Bean
    @ConditionalOnMissingBean
    public AdminAuthorizer adminAuthorizer() {
        return new DefaultAdminAuthorizer(properties.getAdminRolePermission());
    }

    /**
     * token 生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenGenerator tokenGenerator(AdminAuthorizer adminAuthorizer, SignatureConfiguration signatureConfiguration,
                                         EncryptionConfiguration encryptionConfiguration) {
        return new DefaultJwtTokenGenerator(adminAuthorizer, signatureConfiguration, encryptionConfiguration)
                .setExpireTime(properties.getExpireTime());
    }

    /**
     * 全局安全管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityManager cookieContext(TokenGenerator tokenGenerator) {
        return new SecurityManager(tokenGenerator, properties.getTokenLocation(), properties.getCookie());
    }

    /**
     * 登出操作类
     */
    @Bean
    @ConditionalOnMissingBean
    public LogoutHandler logoutHandler(SecurityManager securityManager) {
        return new DefaultLogoutHandler(securityManager);
    }

    /**
     * ajax 判定器
     */
    @Bean
    @ConditionalOnMissingBean
    public AjaxRequestResolver ajaxRequestResolver() {
        return new DefaultAjaxRequestResolver();
    }

    /**
     * 获取以及验证用户权限相关
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationProfile authorizationContext() {
        return new DefaultAuthorizationProfile();
    }

    /**
     * 异常处理类
     */
    @Bean
    @ConditionalOnMissingBean
    public HttpActionHandler httpActionHandler() {
        return new DefaultHttpActionHandler();
    }
}
