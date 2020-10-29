package com.baomidou.shaun.autoconfigure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.authority.DefaultAuthorityManager;
import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.extractor.TokenExtractor;
import com.baomidou.shaun.core.filter.CallbackFilter;
import com.baomidou.shaun.core.filter.LogoutFilter;
import com.baomidou.shaun.core.filter.SecurityFilter;
import com.baomidou.shaun.core.filter.SfLoginFilter;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.handler.DefaultLogoutHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.mgt.DefaultProfileManager;
import com.baomidou.shaun.core.mgt.ProfileManager;
import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.util.WebUtil;

import lombok.RequiredArgsConstructor;

/**
 * @author miemie
 * @since 2019-07-18
 */
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ShaunProperties.class)
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
        return new TokenExtractor(properties.getTokenLocation(), properties.getHeader(), properties.getParameter(), properties.getCookie());
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
    public Config config(ShaunProperties properties, AuthorityManager authorityManager, LogoutHandler logoutHandler,
                         ObjectProvider<ProfileManager> profileManagerProvider,
                         ObjectProvider<AjaxRequestResolver> ajaxRequestResolverProvider,
                         ObjectProvider<List<Authorizer>> authorizerProvider,
                         ObjectProvider<List<Matcher>> matcherProvider,
                         ObjectProvider<HttpActionHandler> httpActionHandlerProvider) {
        Config config = new Config();
        config.setCookie(properties.getCookie());
        config.setExpireTime(properties.getExpireTime());
        config.setAuthorityManager(authorityManager);
        config.setLogoutHandler(logoutHandler);
        if (StringUtils.hasText(properties.getLoginUrl())) {
            config.setStateless(false);
            config.setLoginUrl(properties.getLoginUrl());
        }
        config.authorizerNamesAppend(properties.getAuthorizerNames());
        profileManagerProvider.ifAvailable(config::setProfileManager);
        authorizerProvider.ifAvailable(config::addAuthorizers);
        config.matcherNamesAppend(properties.getMatcherNames());
        matcherProvider.ifAvailable(config::addMatchers);
        httpActionHandlerProvider.ifUnique(config::setHttpActionHandler);
        ajaxRequestResolverProvider.ifUnique(config::setAjaxRequestResolver);
        WebUtil.setEnableSession(properties.isEnableSession());
        return config;
    }

    /**
     * 全局安全管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityManager cookieContext(ShaunProperties properties, Config config) {
        return new SecurityManager(config, properties.getTokenLocation());
    }

    @RequiredArgsConstructor
    @Configuration(proxyBeanMethods = false)
    static class ShaunFilterConfiguration {

        private final ShaunProperties properties;

        @Bean
        @ConditionalOnMissingBean(ShaunFilter.class)
        public List<ShaunFilter> shaunFilters(Config config, SecurityManager securityManager,
                                              ObjectProvider<CallbackHandler> callbackHandlerProvider,
                                              ObjectProvider<List<IndirectClient>> indirectClientsProvider) {
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
                Assert.isTrue(properties.getTokenLocation().enableCookie(),
                        "非前后端分离的项目请标记'tokenLocation'允许'cookie'");
            }
            final List<ShaunFilter> filterList = new ArrayList<>();

            /* securityFilter begin */

            final SecurityFilter securityFilter = new SecurityFilter(pathMatcher);

            filterList.add(securityFilter);
            /* securityFilter end */

            /* logoutFilter begin */
            if (StringUtils.hasText(properties.getLogoutUrl())) {
                final LogoutFilter logoutFilter = new LogoutFilter(new OnlyPathMatcher(properties.getLogoutUrl()));
                logoutFilter.setSecurityManager(securityManager);
                filterList.add(logoutFilter);
            }
            /* logoutFilter end */

            List<IndirectClient> indirectClients = indirectClientsProvider.getIfAvailable();
            if (!CollectionUtils.isEmpty(indirectClients)) {
                Assert.isTrue(!config.isStateless(), "要用三方登录只支持非前后分离的项目");
                final String sfLoginUrl = properties.getSfLoginUrl();
                Assert.hasText(sfLoginUrl, "sfLoginUrl cannot be blank");

                final String callbackUrl = properties.getCallbackUrl();
                Assert.hasText(callbackUrl, "callbackUrl cannot be blank");

                final CallbackHandler callbackHandler = callbackHandlerProvider.getIfAvailable();
                Assert.notNull(callbackHandler, "callbackHandler cannot be null");
                List<Client> clientList = indirectClients.stream()
                        .peek(i -> i.setAjaxRequestResolver(config.getAjaxRequestResolver()))
                        .collect(Collectors.toList());
                Clients clients = new Clients(callbackUrl, clientList);

                final SfLoginFilter sfLoginFilter = new SfLoginFilter(new OnlyPathMatcher(sfLoginUrl));
                sfLoginFilter.setClients(clients);
                filterList.add(sfLoginFilter);

                final CallbackFilter callbackFilter = new CallbackFilter(new OnlyPathMatcher(properties.getCallbackUrl()));
                callbackFilter.setClients(clients);
                callbackFilter.setCallbackHandler(callbackHandlerProvider.getIfAvailable());
                filterList.add(callbackFilter);
            }
            return filterList;
        }
    }
}
