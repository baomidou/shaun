package com.baomidou.shaun.stateless.autoconfigure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.core.util.CommonHelper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.context.J2EContextFactory;
import com.baomidou.shaun.core.context.session.NoSessionStore;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.filter.stateless.StatelessLogoutFilter;
import com.baomidou.shaun.core.filter.stateless.StatelessSecurityFilter;
import com.baomidou.shaun.core.handler.logout.LogoutHandler;
import com.baomidou.shaun.core.interceptor.ShaunInterceptor;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.stateless.autoconfigure.aop.AnnotationAspect;
import com.baomidou.shaun.stateless.autoconfigure.properties.ShaunStatelessProperties;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author miemie
 * @since 2019-07-18
 */
@Data
@AllArgsConstructor
@Configuration
@AutoConfigureAfter(ShaunStatelessAutoConfiguration.class)
public class ShaunStatelessSecurityAutoConfiguration implements WebMvcConfigurer {

    private final ShaunStatelessProperties properties;
    private final ApplicationContext applicationContext;
    private final Authenticator<TokenCredentials> authenticator;
    private final CredentialsExtractor<TokenCredentials> credentialsExtractor;
    private final J2EContextFactory j2EContextFactory;
    private final ObjectProvider<LogoutHandler> logoutHandlerObjectProvider;

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
        String authorizers = properties.getAuthorizers();
        Map<String, Authorizer> authorizeMap = applicationContext.getBeansOfType(Authorizer.class);
        if (!CollectionUtils.isEmpty(authorizeMap)) {
            String s = String.join(Pac4jConstants.ELEMENT_SEPRATOR, authorizeMap.keySet());
            if (StringUtils.hasText(authorizers)) {
                authorizers += (Pac4jConstants.ELEMENT_SEPRATOR + s);
            } else {
                authorizers = s;
            }
        }

        StatelessSecurityFilter securityFilter = new StatelessSecurityFilter();
        securityFilter.setPathMatcher(pathMatcher);
        securityFilter.setAuthorizerMap(authorizeMap);
        securityFilter.setAuthorizers(authorizers);
        securityFilter.setTokenClient(tokenClient);

        filterList.add(securityFilter);
        /* securityFilter end */

        /* logoutFilter begin */
        if (CommonHelper.isNotBlank(properties.getLogoutUrl())) {
            StatelessLogoutFilter logoutFilter = new StatelessLogoutFilter();
            logoutFilter.setPathMatcher(OnlyPathMatcher.instance(properties.getLogoutUrl()));
            LogoutHandler logoutHandler = logoutHandlerObjectProvider.getIfAvailable();
            logoutFilter.setLogoutExecutor(logoutHandler);

            filterList.add(logoutFilter);
        }

        ShaunInterceptor interceptor = new ShaunInterceptor();
        return interceptor.setJ2EContextFactory(j2EContextFactory).setSessionStore(NoSessionStore.INSTANCE)
                .setFilterList(filterList);
    }

    @Bean
    @ConditionalOnMissingBean
    public AnnotationAspect annotationAspect() {
        return new AnnotationAspect(j2EContextFactory);
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
