package com.baomidou.shaun.stateless.autoconfigure;

import com.baomidou.shaun.autoconfigure.ShaunWebAutoConfiguration;
import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.filter.LogoutFilter;
import com.baomidou.shaun.core.filter.SecurityFilter;
import com.baomidou.shaun.core.filter.chain.DefaultShaunFilterChain;
import com.baomidou.shaun.core.filter.chain.ShaunFilterChain;
import com.baomidou.shaun.core.handler.DefaultLogoutHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.mgt.ProfileManager;
import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.spring.boot.ShaunBeanConfigurationSupport;
import lombok.RequiredArgsConstructor;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author miemie
 * @since 2019-07-18
 */
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ShaunProperties.class)
@AutoConfigureBefore(ShaunWebAutoConfiguration.class)
public class ShaunBeanAutoConfiguration extends ShaunBeanConfigurationSupport {

    private final ShaunProperties properties;

    /**
     * jwt 签名类
     */
    @Bean
    @ConditionalOnMissingBean
    public SignatureConfiguration signatureConfiguration() {
        return getSignatureConfiguration(properties.getSalt());
    }

    /**
     * jwt 加密类
     */
    @Bean
    @ConditionalOnMissingBean
    public EncryptionConfiguration encryptionConfiguration() {
        return getEncryptionConfiguration(properties.getSalt());
    }

    /**
     * token 取的方式
     */
    @Bean
    @ConditionalOnMissingBean
    public CredentialsExtractor<TokenCredentials> credentialsExtractor() {
        return getCredentialsExtractor(properties.getTokenLocation(), properties.getHeader(), properties.getCookie(), properties.getParameter());
    }

    /**
     * profile 管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public ProfileManager profileManager(SignatureConfiguration signatureConfiguration,
                                         EncryptionConfiguration encryptionConfiguration,
                                         CredentialsExtractor<TokenCredentials> credentialsExtractor) {
        return getProfileManager(signatureConfiguration, encryptionConfiguration, credentialsExtractor);
    }

    /**
     * 获取以及验证用户权限相关
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthorityManager authorityManager() {
        return getAuthorityManager(properties.getSkipAuthenticationRolePermission());
    }

    /**
     * 登出操作类
     */
    @Bean
    @ConditionalOnMissingBean
    public LogoutHandler logoutHandler() {
        return new DefaultLogoutHandler(properties.getTokenLocation(), properties.getCookie());
    }

    @Bean
    @ConditionalOnMissingBean
    public Config config(AuthorityManager authorityManager, LogoutHandler logoutHandler, ProfileManager profileManager,
                         ObjectProvider<AjaxRequestResolver> ajaxRequestResolverProvider,
                         ObjectProvider<List<Authorizer>> authorizerProvider,
                         ObjectProvider<List<Matcher>> matcherProvider,
                         ObjectProvider<HttpActionHandler> httpActionHandlerProvider) {
        Config config = new Config();
        config.setCookie(properties.getCookie());
        config.setExpireTime(properties.getExpireTime());
        config.setAuthorityManager(authorityManager);
        config.setLogoutHandler(logoutHandler);
        config.setLoginUrl(properties.getLoginUrl());
        config.authorizerNamesAppend(properties.getAuthorizerNames());
        config.setProfileManager(profileManager);
        authorizerProvider.ifAvailable(config::addAuthorizers);
        config.matcherNamesAppend(properties.getMatcherNames());
        matcherProvider.ifAvailable(config::addMatchers);
        httpActionHandlerProvider.ifUnique(config::setHttpActionHandler);
        ajaxRequestResolverProvider.ifUnique(config::setAjaxRequestResolver);
        return config;
    }

    /**
     * 全局安全管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityManager securityManager(Config config) {
        return getSecurityManager(config, properties.getTokenLocation());
    }

    @Bean
    @ConditionalOnMissingBean
    public ShaunFilterChain shaunFilterChain(SecurityManager securityManager) {
        DefaultShaunFilterChain chain = new DefaultShaunFilterChain();

        /* securityFilter begin */
        final PathMatcher pathMatcher = new PathMatcher();
        if (!CollectionUtils.isEmpty(properties.getExcludePath())) {
            properties.getExcludePath().forEach(pathMatcher::excludePath);
        }
        if (!CollectionUtils.isEmpty(properties.getExcludeBranch())) {
            properties.getExcludeBranch().forEach(pathMatcher::excludeBranch);
        }
        if (!CollectionUtils.isEmpty(properties.getExcludeRegex())) {
            properties.getExcludeBranch().forEach(pathMatcher::excludeRegex);
        }
        if (StringUtils.hasText(properties.getLoginUrl())) {
            pathMatcher.excludePath(properties.getLoginUrl());
        }
        final SecurityFilter securityFilter = new SecurityFilter(pathMatcher);
        /* securityFilter end */

        chain.addShaunFilter(securityFilter);

        /* logoutFilter begin */
        if (StringUtils.hasText(properties.getLogoutUrl())) {
            final LogoutFilter logoutFilter = new LogoutFilter(new OnlyPathMatcher(properties.getLogoutUrl()));
            logoutFilter.setSecurityManager(securityManager);
            chain.addShaunFilter(logoutFilter);
        }
        /* logoutFilter end */

        return chain;
    }
}
