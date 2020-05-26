package com.baomidou.shaun.autoconfigure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.baomidou.shaun.autoconfigure.intercept.MethodSecurityAdvisor;
import com.baomidou.shaun.autoconfigure.intercept.MethodSecurityInterceptor;
import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.filter.CallbackFilter;
import com.baomidou.shaun.core.filter.LogoutFilter;
import com.baomidou.shaun.core.filter.SecurityFilter;
import com.baomidou.shaun.core.filter.SfLoginFilter;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.util.JEEContextUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author miemie
 * @since 2019-07-18
 */
@Data
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ShaunAutoConfiguration.class)
public class ShaunFilterAutoConfiguration {

    private final ShaunProperties properties;
    private final TokenClient tokenClient;
    private final SecurityManager securityManager;
    private final ObjectProvider<AjaxRequestResolver> ajaxRequestResolverProvider;
    private final ObjectProvider<HttpActionHandler> httpActionHandlerProvider;
    private final ObjectProvider<CallbackHandler> callbackHandlerProvider;
    private final ObjectProvider<List<Authorizer>> authorizerProvider;
    private final ObjectProvider<List<Matcher>> matcherProvider;
    private final ObjectProvider<List<IndirectClient>> indirectClientsProvider;

    @Bean
    @ConditionalOnMissingBean
    public Config config() {
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

    @Bean
    @ConditionalOnMissingBean(ShaunFilter.class)
    public List<ShaunFilter> shaunFilters(Config config) {
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

    @Bean
    public MethodSecurityAdvisor shaunMethodSecurityAdvisor(AuthorityManager authorityManager) {
        MethodSecurityAdvisor advisor = new MethodSecurityAdvisor();
        MethodSecurityInterceptor interceptor = new MethodSecurityInterceptor(authorityManager);
        advisor.setAdvice(interceptor);
        return advisor;
    }
}
