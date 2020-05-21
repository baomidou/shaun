package com.baomidou.shaun.autoconfigure;

import com.baomidou.shaun.autoconfigure.intercept.MethodSecurityAdvisor;
import com.baomidou.shaun.autoconfigure.intercept.MethodSecurityInterceptor;
import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.enums.TokenLocation;
import com.baomidou.shaun.core.filter.*;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.mgt.SecurityManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.pac4j.core.util.CommonHelper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author miemie
 * @since 2019-07-18
 */
@Data
@AllArgsConstructor
@Configuration
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
    public Config config(AuthorityManager authorityManager) {
        Config config = new Config();
        config.setTokenClient(tokenClient);
        config.setAuthorityManager(authorityManager);
        if (CommonHelper.isNotBlank(properties.getLoginUrl())) {
            config.setStateless(false);
            config.setLoginUrl(properties.getLoginUrl());
        }
        config.authorizerNamesAppend(properties.getAuthorizerNames());
        authorizerProvider.ifAvailable(config::addAuthorizers);
        config.matcherNamesAppend(properties.getMatcherNames());
        matcherProvider.ifAvailable(config::addMatchers);
        httpActionHandlerProvider.ifUnique(config::setHttpActionHandler);
        ajaxRequestResolverProvider.ifUnique(config::setAjaxRequestResolver);
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

        if (CommonHelper.isNotBlank(properties.getLoginUrl())) {
            pathMatcher.excludePath(properties.getLoginUrl());
            CommonHelper.assertTrue(properties.getTokenLocation() == TokenLocation.COOKIE,
                    "非前后端分离的项目请设置 tokenLocation 值为 \"cookie\"");
        }
        final List<ShaunFilter> filterList = new ArrayList<>();

        /* securityFilter begin */

        final SecurityFilter securityFilter = new SecurityFilter(pathMatcher);

        filterList.add(securityFilter);
        /* securityFilter end */

        /* logoutFilter begin */
        if (CommonHelper.isNotBlank(properties.getLogoutUrl())) {
            final LogoutFilter logoutFilter = new LogoutFilter(new OnlyPathMatcher(properties.getLogoutUrl()));
            logoutFilter.setSecurityManager(securityManager);
            filterList.add(logoutFilter);
        }
        /* logoutFilter end */

        List<IndirectClient> indirectClients = indirectClientsProvider.getIfAvailable();
        if (CommonHelper.isNotEmpty(indirectClients)) {
            CommonHelper.assertTrue(!config.isStateless(), "要用三方登录只支持非前后分离的项目");
            final String sfLoginUrl = properties.getSfLoginUrl();
            CommonHelper.assertNotBlank("sfLoginUrl", sfLoginUrl);
            final String callbackUrl = properties.getCallbackUrl();
            CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
            final String indexUrl = properties.getIndexUrl();
            CommonHelper.assertNotBlank("indexUrl", indexUrl);

            final CallbackHandler callbackHandler = callbackHandlerProvider.getIfAvailable();
            CommonHelper.assertNotNull("callbackHandler", callbackHandler);
            List<Client> clientList = indirectClients.stream().peek(i -> i.setAjaxRequestResolver(config.getAjaxRequestResolver()))
                    .collect(Collectors.toList());
            Clients clients = new Clients(callbackUrl, clientList);

            final SfLoginFilter sfLoginFilter = new SfLoginFilter(new OnlyPathMatcher(sfLoginUrl));
            sfLoginFilter.setClients(clients);
            filterList.add(sfLoginFilter);

            final CallbackFilter callbackFilter = new CallbackFilter(new OnlyPathMatcher(properties.getCallbackUrl()));
            callbackFilter.setClients(clients);
            callbackFilter.setCallbackHandler(callbackHandlerProvider.getIfAvailable());
            callbackFilter.setIndexUrl(indexUrl);
            callbackFilter.setSecurityManager(securityManager);
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
