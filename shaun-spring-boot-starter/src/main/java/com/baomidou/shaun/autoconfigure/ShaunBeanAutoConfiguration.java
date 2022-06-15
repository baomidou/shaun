/*
 * Copyright 2019-2022 baomidou (wonderming@vip.qq.com)
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

import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.baomidou.shaun.autoconfigure.properties.ActuatorProperties;
import com.baomidou.shaun.autoconfigure.properties.SecurityProperties;
import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.autoconfigure.properties.ThirdPartyAuthProperties;
import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.authority.DefaultAuthorityManager;
import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.credentials.extractor.DefaultTokenCredentialsExtractor;
import com.baomidou.shaun.core.credentials.extractor.TokenCredentialsExtractor;
import com.baomidou.shaun.core.filter.ActuatorFilter;
import com.baomidou.shaun.core.filter.LogoutFilter;
import com.baomidou.shaun.core.filter.SecurityFilter;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.filter.ThirdPartyAuthLoginFilter;
import com.baomidou.shaun.core.filter.ThirdPartyCallbackFilter;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.intercept.support.DefaultShaunFilterChain;
import com.baomidou.shaun.core.intercept.support.ShaunFilterChain;
import com.baomidou.shaun.core.jwt.DefaultJwtTypeSelector;
import com.baomidou.shaun.core.jwt.JwtTypeSelector;
import com.baomidou.shaun.core.matching.matcher.IncludePathMatcher;
import com.baomidou.shaun.core.mgt.JwtProfileTokenManager;
import com.baomidou.shaun.core.mgt.ProfileStateManager;
import com.baomidou.shaun.core.mgt.ProfileTokenManager;
import com.baomidou.shaun.core.mgt.SecurityManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author miemie
 * @since 2019-07-18
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ShaunProperties.class)
@AutoConfigureBefore(ShaunWebAutoConfiguration.class)
public class ShaunBeanAutoConfiguration {

    private final ShaunProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public CoreConfig coreConfig(ObjectProvider<AuthorityManager> authorityManagerProvider,
                                 ObjectProvider<ProfileTokenManager> profileTokenManagerProvider,
                                 ObjectProvider<ProfileStateManager> profileStateManagerProvider,
                                 ObjectProvider<LogoutHandler> logoutHandlerProvider,
                                 ObjectProvider<AjaxRequestResolver> ajaxRequestResolverProvider,
                                 ObjectProvider<Authorizer> authorizerProvider,
                                 ObjectProvider<Matcher> matcherProvider,
                                 ObjectProvider<HttpActionHandler> httpActionHandlerProvider) {
        CoreConfig coreConfig = new CoreConfig();
        coreConfig.setStateless(properties.isStateless());
        coreConfig.setSessionOn(properties.isSessionOn());

        httpActionHandlerProvider.ifAvailable(coreConfig::setHttpActionHandler);
        ajaxRequestResolverProvider.ifAvailable(coreConfig::setAjaxRequestResolver);

        String loginUrl = properties.getLoginUrl();
        if (!coreConfig.isStateless()) {
            Assert.hasText(loginUrl, "loginUrl must not black when stateful");
        }
        coreConfig.setLoginUrl(loginUrl);

        coreConfig.matcherNamesAppend(properties.getMatcherNames());
        matcherProvider.stream().forEach(coreConfig::addMatcher);

        if (properties.getSecurity().isEnable()) {
            coreConfig.authorizerNamesAppend(properties.getSecurity().getAuthorizerNames());
            authorizerProvider.stream().forEach(coreConfig::addAuthorizer);
            coreConfig.setProfileTokenManager(profileTokenManagerProvider.getIfAvailable());
            coreConfig.setAuthorityManager(authorityManagerProvider.getIfAvailable());
            profileStateManagerProvider.ifAvailable(coreConfig::setProfileStateManager);
            coreConfig.setTokenLocation(properties.getSecurity().getExtractor().getLocation());
            coreConfig.setCookie(properties.getSecurity().getExtractor().getCookie());
            coreConfig.setExpireTime(properties.getSecurity().getJwt().getExpireTime());
            logoutHandlerProvider.ifAvailable(coreConfig::setLogoutHandler);
        }
        return coreConfig;
    }

    /**
     * 全局安全管理器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "shaun.security.enable", havingValue = "true", matchIfMissing = true)
    public SecurityManager securityManager(CoreConfig coreConfig) {
        return new SecurityManager(coreConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public ShaunFilterChain shaunFilterChain(CoreConfig coreConfig,
                                             ObjectProvider<CallbackHandler> callbackHandlerProvider,
                                             ObjectProvider<IndirectClient> indirectClientsProvider,
                                             ObjectProvider<ShaunFilter> otherShaunFilterProvider) {
        DefaultShaunFilterChain chain = new DefaultShaunFilterChain();

        /* securityFilter begin */
        final PathMatcher securityPathMatcher = new PathMatcher();
        SecurityProperties security = properties.getSecurity();
        SecurityProperties.ExcludePath path = security.getExcludePath();
        if (!CollectionUtils.isEmpty(path.getPath())) {
            path.getPath().forEach(securityPathMatcher::excludePath);
        }
        if (!CollectionUtils.isEmpty(path.getBranch())) {
            path.getBranch().forEach(securityPathMatcher::excludeBranch);
        }
        if (!CollectionUtils.isEmpty(path.getRegex())) {
            path.getRegex().forEach(securityPathMatcher::excludeRegex);
        }
        if (coreConfig.getLoginUrl() != null) {
            securityPathMatcher.excludePath(coreConfig.getLoginUrl());
        }
        if (security.isEnable()) {
            log.info("security is enable");
            final SecurityFilter securityFilter = new SecurityFilter(securityPathMatcher);
            chain.addShaunFilter(securityFilter);
            /* logoutFilter begin */
            if (StringUtils.hasText(security.getLogoutUrl())) {
                chain.addShaunFilter(new LogoutFilter(new IncludePathMatcher().includePath(security.getLogoutUrl())));
            }
            /* logoutFilter end */
        } else {
            log.info("security is not enable");
        }
        /* securityFilter end */

        /* actuatorFilter begin */
        ActuatorProperties actuator = properties.getActuator();
        if (ClassUtils.isPresent("org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration",
                getClass().getClassLoader()) && actuator.isEnable()) {
            securityPathMatcher.excludeBranch(actuator.getBranch());
            ActuatorFilter actuatorFilter = new ActuatorFilter(new IncludePathMatcher().includeBranch(actuator.getBranch()));
            actuatorFilter.setUsername(actuator.getUsername());
            actuatorFilter.setPassword(actuator.getPassword());
            chain.addShaunFilter(actuatorFilter);
        }
        /* actuatorFilter end */

        /* other begin */
        ThirdPartyAuthProperties thirdParty = properties.getThirdParty();
        if (!coreConfig.isStateless() && thirdParty.isEnable()) {
            List<Client> indirectClients = indirectClientsProvider.stream().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(indirectClients)) {
                final String sfLoginUrl = thirdParty.getLoginUrl();
                Assert.hasText(sfLoginUrl, "thirdParty.LoginUrl must not blank");

                final String callbackUrl = thirdParty.getCallbackUrl();
                Assert.hasText(callbackUrl, "thirdParty.callbackUrl must not blank");

                final CallbackHandler callbackHandler = callbackHandlerProvider.getIfAvailable();
                Assert.notNull(callbackHandler, "callbackHandler must not null");

                final Clients clients = new Clients(callbackUrl, indirectClients);
                clients.setAjaxRequestResolver(coreConfig.getAjaxRequestResolver());
                clients.setUrlResolver(new DefaultUrlResolver(true));

                final ThirdPartyAuthLoginFilter thirdPartyAuthLoginFilter = new ThirdPartyAuthLoginFilter(new IncludePathMatcher().includePath(sfLoginUrl));
                thirdPartyAuthLoginFilter.setClients(clients);
                chain.addShaunFilter(thirdPartyAuthLoginFilter);

                final ThirdPartyCallbackFilter thirdPartyCallbackFilter = new ThirdPartyCallbackFilter(new IncludePathMatcher().includePath(callbackUrl));
                thirdPartyCallbackFilter.setClients(clients);
                thirdPartyCallbackFilter.setCallbackHandler(callbackHandler);
                chain.addShaunFilter(thirdPartyCallbackFilter);
            }
        }
        otherShaunFilterProvider.stream().forEach(chain::addShaunFilter);
        /* other end */
        return chain;
    }

    @RequiredArgsConstructor
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "shaun.security.enable", havingValue = "true", matchIfMissing = true)
    public static class ShaunSecurityBeanConfiguration {

        private final ShaunProperties properties;

        @Bean
        @ConditionalOnMissingBean
        public JwtTypeSelector jwtTypeSelector() {
            SecurityProperties.Jwt jwt = properties.getSecurity().getJwt();
            return new DefaultJwtTypeSelector(jwt.getType(), jwt.getSalt());
        }

        /**
         * token 取的方式
         */
        @Bean
        @ConditionalOnMissingBean
        public TokenCredentialsExtractor credentialsExtractor() {
            SecurityProperties.Extractor extractor = properties.getSecurity().getExtractor();
            return new DefaultTokenCredentialsExtractor(extractor.getLocation(), extractor.getHeader(), extractor.getCookie(), extractor.getParameter());
        }

        /**
         * profile 管理器
         */
        @Bean
        @ConditionalOnMissingBean
        public ProfileTokenManager profileTokenManager(JwtTypeSelector jwtTypeSelector, TokenCredentialsExtractor credentialsExtractor) {
            return new JwtProfileTokenManager(jwtTypeSelector, credentialsExtractor);
        }

        /**
         * 获取以及验证用户权限相关
         */
        @Bean
        @ConditionalOnMissingBean
        public AuthorityManager authorityManager() {
            return new DefaultAuthorityManager(properties.getSecurity().getSkipAuthenticationRolePermission());
        }
    }
}
