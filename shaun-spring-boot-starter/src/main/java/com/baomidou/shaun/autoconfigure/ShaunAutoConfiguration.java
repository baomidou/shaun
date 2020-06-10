package com.baomidou.shaun.autoconfigure;

import com.baomidou.shaun.autoconfigure.intercept.MethodSecurityAdvisor;
import com.baomidou.shaun.autoconfigure.intercept.MethodSecurityInterceptor;
import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.authority.DefaultAuthorityManager;
import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.extractor.TokenExtractor;
import com.baomidou.shaun.core.filter.*;
import com.baomidou.shaun.core.generator.DefaultJwtTokenGenerator;
import com.baomidou.shaun.core.generator.TokenGenerator;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.handler.DefaultLogoutHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.models.ShaunInterceptor;
import com.baomidou.shaun.core.models.ShaunRequestFilter;
import com.baomidou.shaun.core.util.JEEContextUtil;
import lombok.RequiredArgsConstructor;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author miemie
 * @since 2019-07-18
 */
@Configuration(proxyBeanMethods = false)
public class ShaunAutoConfiguration {

    @RequiredArgsConstructor
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(ShaunProperties.class)
    static class ShaunConfiguration {

        /**
         * jwt 签名类
         */
        @Bean
        @ConditionalOnMissingBean
        SignatureConfiguration signatureConfiguration(ShaunProperties properties) {
            return new SecretSignatureConfiguration(properties.getSalt());
        }

        /**
         * jwt 加密类
         */
        @Bean
        @ConditionalOnMissingBean
        EncryptionConfiguration encryptionConfiguration(ShaunProperties properties) {
            return new SecretEncryptionConfiguration(properties.getSalt());
        }

        /**
         * token 取的方式
         */
        @Bean
        @ConditionalOnMissingBean
        CredentialsExtractor<TokenCredentials> credentialsExtractor(ShaunProperties properties) {
            return new TokenExtractor(properties.getTokenLocation(), properties.getHeader(), properties.getParameter(), properties.getCookie());
        }

        /**
         * token 检验器
         */
        @Bean
        @ConditionalOnMissingBean
        Authenticator<TokenCredentials> authenticator(SignatureConfiguration signatureConfiguration,
                                                      EncryptionConfiguration encryptionConfiguration) {
            return new JwtAuthenticator(signatureConfiguration, encryptionConfiguration);
        }

        /**
         * 获取以及验证用户权限相关
         */
        @Bean
        @ConditionalOnMissingBean
        AuthorityManager authorityManager(ShaunProperties properties) {
            return new DefaultAuthorityManager(properties.getSkipAuthenticationRolePermission());
        }

        /**
         * token 生成器
         */
        @Bean
        @ConditionalOnMissingBean
        TokenGenerator tokenGenerator(AuthorityManager authorityManager, ShaunProperties properties,
                                      SignatureConfiguration signatureConfiguration,
                                      EncryptionConfiguration encryptionConfiguration) {
            DefaultJwtTokenGenerator generator = new DefaultJwtTokenGenerator(authorityManager, signatureConfiguration,
                    encryptionConfiguration);
            generator.setDefaultExpireTime(properties.getExpireTime());
            return generator;
        }

        /**
         * 登出操作类
         */
        @Bean
        @ConditionalOnMissingBean
        LogoutHandler logoutHandler(ShaunProperties properties) {
            return new DefaultLogoutHandler(properties.getTokenLocation(), properties.getCookie());
        }

        /**
         * client
         */
        @Bean
        @ConditionalOnMissingBean
        TokenClient tokenClient(CredentialsExtractor<TokenCredentials> credentialsExtractor,
                                Authenticator<TokenCredentials> authenticator) {
            return new TokenClient(credentialsExtractor, authenticator);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class ShaunAdvisorConfiguration {

        @Bean
        @ConditionalOnMissingBean
        MethodSecurityInterceptor shaunMethodSecurityInterceptor() {
            return new MethodSecurityInterceptor();
        }

        @Bean
        @ConditionalOnMissingBean
        MethodSecurityAdvisor shaunMethodSecurityAdvisor(MethodSecurityInterceptor interceptor) {
            MethodSecurityAdvisor advisor = new MethodSecurityAdvisor();
            advisor.setAdvice(interceptor);
            return advisor;
        }
    }

    @RequiredArgsConstructor
    @Configuration(proxyBeanMethods = false)
    @AutoConfigureAfter(ShaunConfiguration.class)
    static class SecurityManagerConfiguration {

        /**
         * 全局安全管理器
         */
        @Bean
        @ConditionalOnMissingBean
        SecurityManager cookieContext(ShaunProperties properties, TokenGenerator tokenGenerator, LogoutHandler logoutHandler) {
            return new SecurityManager(tokenGenerator, properties.getTokenLocation(), properties.getCookie(), logoutHandler);
        }
    }

    @RequiredArgsConstructor
    @Configuration(proxyBeanMethods = false)
    @AutoConfigureAfter(ShaunConfiguration.class)
    static class ShaunConfigConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Config config(ShaunProperties properties, TokenClient tokenClient,
                             ObjectProvider<AjaxRequestResolver> ajaxRequestResolverProvider,
                             ObjectProvider<List<Authorizer>> authorizerProvider,
                             ObjectProvider<List<Matcher>> matcherProvider,
                             ObjectProvider<HttpActionHandler> httpActionHandlerProvider) {
            Config config = new Config();
            config.setTokenClient(tokenClient);
            if (StringUtils.hasText(properties.getLoginUrl())) {
                config.setStateless(false);
                config.setLoginUrl(properties.getLoginUrl());
            }
            config.authorizerNamesAppend(properties.getAuthorizerNames());
            authorizerProvider.ifAvailable(config::addAuthorizers);
            config.matcherNamesAppend(properties.getMatcherNames());
            matcherProvider.ifAvailable(config::addMatchers);
            httpActionHandlerProvider.ifUnique(config::setHttpActionHandler);
            ajaxRequestResolverProvider.ifUnique(config::setAjaxRequestResolver);
            JEEContextUtil.setEnableSession(properties.isEnableSession());
            return config;
        }
    }

    @RequiredArgsConstructor
    @Configuration(proxyBeanMethods = false)
    @AutoConfigureAfter(SecurityManagerConfiguration.class)
    static class ShaunFilterConfiguration {

        @Bean
        @ConditionalOnMissingBean(ShaunFilter.class)
        public List<ShaunFilter> shaunFilters(ShaunProperties properties, Config config, SecurityManager securityManager,
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

    @RequiredArgsConstructor
    @Configuration(proxyBeanMethods = false)
    @AutoConfigureAfter(ShaunFilterConfiguration.class)
    static class ShaunModelConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnClass(HandlerInterceptor.class)
        @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "interceptor", matchIfMissing = true)
        ShaunInterceptor shaunInterceptor(Config config, List<ShaunFilter> filters) {
            ShaunInterceptor interceptor = new ShaunInterceptor();
            return interceptor.setFilterList(filters).setConfig(config);
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnClass(OncePerRequestFilter.class)
        @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "web_filter")
        ShaunRequestFilter shaunOncePerRequestFilter(Config config, List<ShaunFilter> filters) {
            ShaunRequestFilter oncePerRequestFilter = new ShaunRequestFilter();
            return oncePerRequestFilter.setFilterList(filters).setConfig(config);
        }

        @Configuration(proxyBeanMethods = false)
        @AutoConfigureAfter(ShaunModelConfiguration.class)
        @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
        @ConditionalOnClass({HandlerInterceptor.class, WebMvcConfigurer.class})
        @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "interceptor", matchIfMissing = true)
        static class ShaunWebConfiguration {

            @RequiredArgsConstructor
            @Configuration(proxyBeanMethods = false)
            public static class ShaunWebMvcConfiguration implements WebMvcConfigurer {

                private final ShaunInterceptor shaunInterceptor;

                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(shaunInterceptor).addPathPatterns("/**");
                }
            }
        }
    }
}
