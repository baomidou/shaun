package com.baomidou.shaun.autoconfigure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.baomidou.shaun.autoconfigure.aop.AnnotationAspect;
import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.core.authorizer.AuthorizationProfile;
import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.context.GlobalConfig;
import com.baomidou.shaun.core.enums.TokenLocation;
import com.baomidou.shaun.core.filter.CallbackFilter;
import com.baomidou.shaun.core.filter.LogoutFilter;
import com.baomidou.shaun.core.filter.SecurityFilter;
import com.baomidou.shaun.core.filter.SfLoginFilter;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.interceptor.ShaunInterceptor;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.mgt.SecurityManager;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author miemie
 * @since 2019-07-18
 */
@Data
@AllArgsConstructor
@Configuration
@AutoConfigureAfter(ShaunAutoConfiguration.class)
public class ShaunSecurityAutoConfiguration implements WebMvcConfigurer {

    private final ShaunProperties properties;
    private final Authenticator<TokenCredentials> authenticator;
    private final AjaxRequestResolver ajaxRequestResolver;
    private final SecurityManager securityManager;
    private final CredentialsExtractor<TokenCredentials> credentialsExtractor;
    private final ObjectProvider<LogoutHandler> logoutHandlerProvider;
    private final ObjectProvider<CallbackHandler> callbackHandlerProvider;
    private final ObjectProvider<List<Authorizer>> authorizerProvider;
    private final ObjectProvider<List<IndirectClient>> indirectClientsProvider;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(shaunInterceptor()).addPathPatterns("/**");
    }

    @Bean
    @ConditionalOnMissingBean
    public ShaunInterceptor shaunInterceptor() {
        GlobalConfig.setAjaxRequestResolver(ajaxRequestResolver);
        if (CommonHelper.isNotBlank(properties.getLoginUrl())) {
            GlobalConfig.setStateless(false);
            GlobalConfig.setLoginUrl(properties.getLoginUrl());
            CommonHelper.assertTrue(properties.getTokenLocation() == TokenLocation.COOKIE,
                    "非前后端分离的项目请设置 tokenLocation 值为 \"cookie\"");
        }
        final TokenClient tokenClient = new TokenClient(credentialsExtractor, authenticator);

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

        final List<ShaunFilter> filterList = new ArrayList<>();

        /* securityFilter begin */
        String authorizers = properties.getAuthorizers();
        final List<Authorizer> authorizerList = authorizerProvider.getIfAvailable();
        final Map<String, Authorizer> authorizeMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(authorizerList)) {
            for (Authorizer authorizer : authorizerList) {
                authorizeMap.put(authorizer.getClass().getSimpleName(), authorizer);
            }
            String s = String.join(Pac4jConstants.ELEMENT_SEPARATOR, authorizeMap.keySet());
            if (StringUtils.hasText(authorizers)) {
                authorizers += (Pac4jConstants.ELEMENT_SEPARATOR + s);
            } else {
                authorizers = s;
            }
        }

        final SecurityFilter securityFilter = new SecurityFilter();
        securityFilter.setPathMatcher(pathMatcher);
        securityFilter.setAuthorizerMap(authorizeMap);
        securityFilter.setAuthorizers(authorizers);
        securityFilter.setTokenClient(tokenClient);

        filterList.add(securityFilter);
        /* securityFilter end */

        /* logoutFilter begin */
        if (CommonHelper.isNotBlank(properties.getLogoutUrl())) {
            final LogoutFilter logoutFilter = new LogoutFilter();
            logoutFilter.setPathMatcher(new OnlyPathMatcher(properties.getLogoutUrl()));
            logoutFilter.setLogoutExecutor(logoutHandlerProvider.getIfAvailable());

            filterList.add(logoutFilter);
        }
        /* logoutFilter end */

        List<IndirectClient> indirectClients = indirectClientsProvider.getIfAvailable();
        if (CommonHelper.isNotEmpty(indirectClients)) {
            CommonHelper.assertTrue(!GlobalConfig.isStateless(), "要用三方登录只支持非前后分离的项目");
            final String sfLoginUrl = properties.getSfLoginUrl();
            CommonHelper.assertNotBlank("sfLoginUrl", sfLoginUrl);
            final String callbackUrl = properties.getCallbackUrl();
            CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
            final String indexUrl = properties.getIndexUrl();
            CommonHelper.assertNotBlank("indexUrl", indexUrl);

            final CallbackHandler callbackHandler = callbackHandlerProvider.getIfAvailable();
            CommonHelper.assertNotNull("callbackHandler", callbackHandler);
            List<Client> clientList = indirectClients.stream().peek(i -> i.setAjaxRequestResolver(ajaxRequestResolver))
                    .collect(Collectors.toList());
            Clients clients = new Clients(callbackUrl, clientList);

            final SfLoginFilter sfLoginFilter = new SfLoginFilter();
            sfLoginFilter.setClients(clients);
            sfLoginFilter.setPathMatcher(new OnlyPathMatcher(sfLoginUrl));
            filterList.add(sfLoginFilter);

            final CallbackFilter callbackFilter = new CallbackFilter();
            callbackFilter.setClients(clients);
            callbackFilter.setCallbackHandler(callbackHandlerProvider.getIfAvailable());
            callbackFilter.setIndexUrl(indexUrl);
            callbackFilter.setPathMatcher(new OnlyPathMatcher(properties.getCallbackUrl()));
            callbackFilter.setSecurityManager(securityManager);
            filterList.add(callbackFilter);
        }

        ShaunInterceptor interceptor = new ShaunInterceptor();
        return interceptor.setFilterList(filterList);
    }

    @Bean
    @ConditionalOnMissingBean
    public AnnotationAspect annotationAspect(AuthorizationProfile<UserProfile> authorizationProfile) {
        return new AnnotationAspect(authorizationProfile);
    }
}
