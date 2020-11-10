package com.baomidou.shaun.autoconfigure;

import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.authority.DefaultAuthorityManager;
import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.extractor.TokenExtractor;
import com.baomidou.shaun.core.filter.CallbackFilter;
import com.baomidou.shaun.core.filter.LogoutFilter;
import com.baomidou.shaun.core.filter.SecurityFilter;
import com.baomidou.shaun.core.filter.SfLoginFilter;
import com.baomidou.shaun.core.filter.chain.DefaultShaunFilterChain;
import com.baomidou.shaun.core.filter.chain.ShaunFilterChain;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.mgt.DefaultProfileManager;
import com.baomidou.shaun.core.mgt.ProfileManager;
import com.baomidou.shaun.core.mgt.SecurityManager;
import lombok.RequiredArgsConstructor;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author miemie
 * @since 2019-07-18
 */
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ShaunProperties.class)
@AutoConfigureBefore(ShaunWebAutoConfiguration.class)
public class ShaunBeanAutoConfiguration {

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
     * token 取的方式
     */
    @Bean
    @ConditionalOnMissingBean
    public CredentialsExtractor<TokenCredentials> credentialsExtractor() {
        return new TokenExtractor(properties.getTokenLocation(), properties.getHeader(), properties.getCookie(), properties.getParameter());
    }

    /**
     * profile 管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public ProfileManager profileManager(SignatureConfiguration signatureConfiguration,
                                         EncryptionConfiguration encryptionConfiguration,
                                         CredentialsExtractor<TokenCredentials> credentialsExtractor) {
        return new DefaultProfileManager(signatureConfiguration, encryptionConfiguration, credentialsExtractor);
    }

    /**
     * 获取以及验证用户权限相关
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthorityManager authorityManager() {
        return new DefaultAuthorityManager(properties.getSkipAuthenticationRolePermission());
    }

    @Bean
    @ConditionalOnMissingBean
    public Config config(AuthorityManager authorityManager, ProfileManager profileManager,
                         ObjectProvider<LogoutHandler> logoutHandlerProvider,
                         ObjectProvider<AjaxRequestResolver> ajaxRequestResolverProvider,
                         ObjectProvider<List<Authorizer>> authorizerProvider,
                         ObjectProvider<List<Matcher>> matcherProvider,
                         ObjectProvider<HttpActionHandler> httpActionHandlerProvider) {
        Config config = new Config();
        config.setTokenLocation(properties.getTokenLocation());
        config.setSessionOn(properties.isSessionOn());
        config.setCookie(properties.getCookie());
        config.setExpireTime(properties.getExpireTime());
        config.setAuthorityManager(authorityManager);
        logoutHandlerProvider.ifAvailable(config::setLogoutHandler);
        String loginUrl = properties.getLoginUrl();
        Assert.hasText(loginUrl, "loginUrl must not black");
        config.setLoginUrl(loginUrl);
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
        return new SecurityManager(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public ShaunFilterChain shaunFilterChain(Config config, SecurityManager securityManager,
                                             ObjectProvider<CallbackHandler> callbackHandlerProvider,
                                             ObjectProvider<List<IndirectClient>> indirectClientsProvider) {
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
        if (config.getLoginUrl() != null) {
            pathMatcher.excludePath(config.getLoginUrl());
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

        List<IndirectClient> indirectClients = indirectClientsProvider.getIfAvailable();
        if (!CollectionUtils.isEmpty(indirectClients)) {
            final String sfLoginUrl = properties.getSfLoginUrl();
            Assert.hasText(sfLoginUrl, "sfLoginUrl must not blank");

            final String callbackUrl = properties.getCallbackUrl();
            Assert.hasText(callbackUrl, "callbackUrl must not blank");

            final CallbackHandler callbackHandler = callbackHandlerProvider.getIfAvailable();
            Assert.notNull(callbackHandler, "callbackHandler must not null");
            List<Client> clientList = indirectClients.stream()
                    .peek(i -> i.setAjaxRequestResolver(config.getAjaxRequestResolver()))
                    .collect(Collectors.toList());
            Clients clients = new Clients(callbackUrl, clientList);

            final SfLoginFilter sfLoginFilter = new SfLoginFilter(new OnlyPathMatcher(sfLoginUrl));
            sfLoginFilter.setClients(clients);
            chain.addShaunFilter(sfLoginFilter);

            final CallbackFilter callbackFilter = new CallbackFilter(new OnlyPathMatcher(callbackUrl));
            callbackFilter.setClients(clients);
            callbackFilter.setCallbackHandler(callbackHandlerProvider.getIfAvailable());
            chain.addShaunFilter(callbackFilter);
        }
        return chain;
    }
}
