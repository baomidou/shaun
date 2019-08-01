package com.baomidou.shaun.autoconfigure;

import com.baomidou.shaun.autoconfigure.aop.AnnotationAspect;
import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.context.JEEContextFactory;
import com.baomidou.shaun.core.filter.LogoutFilter;
import com.baomidou.shaun.core.filter.SecurityFilter;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.handler.logout.LogoutHandler;
import com.baomidou.shaun.core.interceptor.ShaunInterceptor;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.profile.ProfileManagerFactory;
import lombok.AllArgsConstructor;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.core.util.CommonHelper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author miemie
 * @since 2019-07-18
 */
@AllArgsConstructor
@Configuration
@AutoConfigureAfter(ShaunAutoConfiguration.class)
public class ShaunSecurityAutoConfiguration implements WebMvcConfigurer {

    private final ShaunProperties properties;
    private final ApplicationContext applicationContext;
    private final Authenticator<TokenCredentials> authenticator;
    private final CredentialsExtractor<TokenCredentials> credentialsExtractor;
    private final SessionStore<J2EContext> sessionStore;
    private final ProfileManagerFactory profileManagerFactory;
    private final JEEContextFactory j2EContextFactory;
    private final LogoutHandler logoutHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(shaunInterceptor()).addPathPatterns("/**");
    }

    @Bean
    @ConditionalOnMissingBean
    public ShaunInterceptor shaunInterceptor() {
        TokenClient tokenClient = new TokenClient(credentialsExtractor, authenticator);

        PathMatcher pathMatcher = new PathMatcher();
        if (!CollectionUtils.isEmpty(properties.getExcludePath())) {
            properties.getExcludePath().forEach(pathMatcher::excludePath);
        }
        if (!CollectionUtils.isEmpty(properties.getExcludeBranch())) {
            properties.getExcludeBranch().forEach(pathMatcher::excludeBranch);
        }
        if (!CollectionUtils.isEmpty(properties.getExcludeRegex())) {
            properties.getExcludeBranch().forEach(pathMatcher::excludeRegex);
        }

        List<ShaunFilter> filterList = new ArrayList<>();

        /* securityFilter begin */
        Clients securityClients = new Clients();
        securityClients.setClients(tokenClient);
        securityClients.setDefaultSecurityClients(tokenClient.getName());

        Config securityConfig = new Config(securityClients);
        String authorizers = properties.getAuthorizers();
        Map<String, Authorizer> authorizeMap = applicationContext.getBeansOfType(Authorizer.class);
        if (!CollectionUtils.isEmpty(authorizeMap)) {
            securityConfig.setAuthorizers(authorizeMap);
            String s = String.join(Pac4jConstants.ELEMENT_SEPRATOR, authorizeMap.keySet());
            if (StringUtils.hasText(authorizers)) {
                authorizers += (Pac4jConstants.ELEMENT_SEPRATOR + s);
            } else {
                authorizers = s;
            }
        }

        securityConfig.setProfileManagerFactory(profileManagerFactory);

        SecurityFilter securityFilter = new SecurityFilter();
        securityFilter.setConfig(securityConfig);
        securityFilter.setAuthorizers(authorizers);
        securityFilter.setPathMatcher(pathMatcher);

        filterList.add(securityFilter);
        /* securityFilter end */

        /* logoutFilter begin */
        if (CommonHelper.isNotBlank(properties.getLogoutUrl())) {
            LogoutFilter logoutFilter = new LogoutFilter();
            logoutFilter.setClient(tokenClient);
            logoutFilter.setPathMatcher(OnlyPathMatcher.instance(properties.getLogoutUrl()));
            logoutFilter.setProfileManagerFactory(profileManagerFactory);
            logoutFilter.setLogoutHandler(logoutHandler);

            filterList.add(logoutFilter);
        }
        /* logoutFilter end */

//        if (!properties.isStateless() && this.hasBean(IndirectClient.class)) {
//            Map<String, IndirectClient> indirectClientMap = applicationContext.getBeansOfType(IndirectClient.class,
//                    false, false);
//
//            Clients sfClients = new Clients(properties.getCallbackUrl(), new ArrayList<>(indirectClientMap.values()));
//            Config sfConfig = new Config(sfClients);
//            sfConfig.setProfileManagerFactory(profileManagerFactory);
//
//            /* threeLandingFilter begin */
//            ThreeLandingFilter threeLandingFilter = new ThreeLandingFilter();
//            threeLandingFilter.setConfig(sfConfig);
//            threeLandingFilter.setThreeLandingUrl(properties.getThreeLandingUrl());
//            /* threeLandingFilter end */
//
//            /* callbackFilter begin */
//            CallbackFilter callbackFilter = new CallbackFilter();
//            callbackFilter.setConfig(sfConfig);
//            callbackFilter.setCallbackUrl(properties.getCallbackUrl());
//            callbackFilter.setIndexUrl(properties.getIndexUrl());
//            CallbackExecutor callbackExecutor = this.getOrDefault(CallbackExecutor.class, () -> CallbackExecutor.DO_NOTHING);
//            callbackFilter.setCallbackExecutor(callbackExecutor);
//            /* callbackFilter end */
//
//            filterList.add(threeLandingFilter);
//            filterList.add(callbackFilter);
//        }

        return new ShaunInterceptor().setJ2EContextFactory(j2EContextFactory).setSessionStore(sessionStore)
                .setFilterList(filterList);
    }

    @Bean
    @ConditionalOnMissingBean
    public AnnotationAspect annotationAspect() {
        return new AnnotationAspect(profileManagerFactory, sessionStore, j2EContextFactory);
    }

    private <T> T getOrDefault(Class<T> clazz, Supplier<T> supplier) {
        if (this.hasBean(clazz)) {
            return applicationContext.getBean(clazz);
        }
        return supplier.get();
    }

    private <T> boolean hasBean(Class<T> clazz) {
        return applicationContext.getBeanNamesForType(clazz, false, false).length > 0;
    }
}
