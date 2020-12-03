/*
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.autoconfigure;

import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.authority.DefaultAuthorityManager;
import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.config.DefaultJwtModelSelector;
import com.baomidou.shaun.core.config.JwtModelSelector;
import com.baomidou.shaun.core.credentials.extractor.DefaultShaunCredentialsExtractor;
import com.baomidou.shaun.core.credentials.extractor.ShaunCredentialsExtractor;
import com.baomidou.shaun.core.filter.CallbackFilter;
import com.baomidou.shaun.core.filter.LogoutFilter;
import com.baomidou.shaun.core.filter.SecurityFilter;
import com.baomidou.shaun.core.filter.SfLoginFilter;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.intercept.support.DefaultShaunFilterChain;
import com.baomidou.shaun.core.intercept.support.ShaunFilterChain;
import com.baomidou.shaun.core.matching.matcher.OnlyPathMatcher;
import com.baomidou.shaun.core.mgt.DefaultProfileTokenManager;
import com.baomidou.shaun.core.mgt.ProfileStateManager;
import com.baomidou.shaun.core.mgt.ProfileTokenManager;
import com.baomidou.shaun.core.mgt.SecurityManager;
import lombok.RequiredArgsConstructor;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.PathMatcher;
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

    @Bean
    @ConditionalOnMissingBean
    public JwtModelSelector jwtModelSelector() {
        return new DefaultJwtModelSelector(properties.getJwtModel(), properties.getSalt());
    }

    /**
     * token 取的方式
     */
    @Bean
    @ConditionalOnMissingBean
    public ShaunCredentialsExtractor credentialsExtractor() {
        return new DefaultShaunCredentialsExtractor(properties.getTokenLocation(), properties.getHeader(), properties.getCookie(), properties.getParameter());
    }

    /**
     * profile 管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public ProfileTokenManager profileTokenManager(JwtModelSelector jwtModelSelector,
                                                   ShaunCredentialsExtractor credentialsExtractor) {
        return new DefaultProfileTokenManager(jwtModelSelector, credentialsExtractor);
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
    public CoreConfig coreConfig(AuthorityManager authorityManager, ProfileTokenManager profileTokenManager,
                                 ObjectProvider<ProfileStateManager> profileStateManagerProvider,
                                 ObjectProvider<LogoutHandler> logoutHandlerProvider,
                                 ObjectProvider<AjaxRequestResolver> ajaxRequestResolverProvider,
                                 ObjectProvider<Authorizer> authorizerProvider,
                                 ObjectProvider<Matcher> matcherProvider,
                                 ObjectProvider<HttpActionHandler> httpActionHandlerProvider) {
        CoreConfig coreConfig = new CoreConfig();
        coreConfig.setStateless(properties.isStateless());
        coreConfig.setSessionOn(properties.isSessionOn());
        coreConfig.setTokenLocation(properties.getTokenLocation());
        coreConfig.setCookie(properties.getCookie());
        coreConfig.setExpireTime(properties.getExpireTime());
        coreConfig.setAuthorityManager(authorityManager);
        profileStateManagerProvider.ifAvailable(coreConfig::setProfileStateManager);
        logoutHandlerProvider.ifAvailable(coreConfig::setLogoutHandler);
        String loginUrl = properties.getLoginUrl();
        if (!coreConfig.isStateless()) {
            Assert.hasText(loginUrl, "loginUrl must not black when stateful");
        }
        coreConfig.setLoginUrl(loginUrl);
        coreConfig.setProfileTokenManager(profileTokenManager);

        coreConfig.authorizerNamesAppend(properties.getAuthorizerNames());
        authorizerProvider.stream().forEach(coreConfig::addAuthorizer);
        coreConfig.matcherNamesAppend(properties.getMatcherNames());
        matcherProvider.stream().forEach(coreConfig::addMatcher);

        httpActionHandlerProvider.ifAvailable(coreConfig::setHttpActionHandler);
        ajaxRequestResolverProvider.ifAvailable(coreConfig::setAjaxRequestResolver);
        return coreConfig;
    }

    /**
     * 全局安全管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityManager securityManager(CoreConfig coreConfig) {
        return new SecurityManager(coreConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public ShaunFilterChain shaunFilterChain(CoreConfig coreConfig, SecurityManager securityManager,
                                             ObjectProvider<CallbackHandler> callbackHandlerProvider,
                                             ObjectProvider<IndirectClient> indirectClientsProvider) {
        DefaultShaunFilterChain chain = new DefaultShaunFilterChain();

        /* securityFilter begin */
        final PathMatcher securityPathMatcher = new PathMatcher();
        if (!CollectionUtils.isEmpty(properties.getExcludePath())) {
            properties.getExcludePath().forEach(securityPathMatcher::excludePath);
        }
        if (!CollectionUtils.isEmpty(properties.getExcludeBranch())) {
            properties.getExcludeBranch().forEach(securityPathMatcher::excludeBranch);
        }
        if (!CollectionUtils.isEmpty(properties.getExcludeRegex())) {
            properties.getExcludeBranch().forEach(securityPathMatcher::excludeRegex);
        }
        if (coreConfig.getLoginUrl() != null) {
            securityPathMatcher.excludePath(coreConfig.getLoginUrl());
        }
        final SecurityFilter securityFilter = new SecurityFilter(securityPathMatcher);
        chain.addShaunFilter(securityFilter);
        /* securityFilter end */

        /* logoutFilter begin */
        if (StringUtils.hasText(properties.getLogoutUrl())) {
            final LogoutFilter logoutFilter = new LogoutFilter(new OnlyPathMatcher(properties.getLogoutUrl()));
            logoutFilter.setSecurityManager(securityManager);
            chain.addShaunFilter(logoutFilter);
        }
        /* logoutFilter end */

        /* other begin */
        if (!coreConfig.isStateless()) {
            List<Client> indirectClients = indirectClientsProvider.stream().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(indirectClients)) {
                final String sfLoginUrl = properties.getSfLoginUrl();
                Assert.hasText(sfLoginUrl, "sfLoginUrl must not blank");

                final String callbackUrl = properties.getCallbackUrl();
                Assert.hasText(callbackUrl, "callbackUrl must not blank");

                final CallbackHandler callbackHandler = callbackHandlerProvider.getIfAvailable();
                Assert.notNull(callbackHandler, "callbackHandler must not null");

                final Clients clients = new Clients(callbackUrl, indirectClients);
                clients.setAjaxRequestResolver(coreConfig.getAjaxRequestResolver());
                clients.setUrlResolver(new DefaultUrlResolver(true));

                final SfLoginFilter sfLoginFilter = new SfLoginFilter(new OnlyPathMatcher(sfLoginUrl));
                sfLoginFilter.setClients(clients);
                chain.addShaunFilter(sfLoginFilter);

                final CallbackFilter callbackFilter = new CallbackFilter(new OnlyPathMatcher(callbackUrl));
                callbackFilter.setClients(clients);
                callbackFilter.setCallbackHandler(callbackHandler);
                chain.addShaunFilter(callbackFilter);
            }
        }
        /* other end */
        return chain;
    }
}
