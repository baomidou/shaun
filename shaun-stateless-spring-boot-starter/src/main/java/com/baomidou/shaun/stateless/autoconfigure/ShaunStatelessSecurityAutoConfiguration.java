package com.baomidou.shaun.stateless.autoconfigure;

import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.interceptor.ShaunInterceptor;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.stateless.autoconfigure.aop.AnnotationAspect;
import com.baomidou.shaun.stateless.autoconfigure.properties.ShaunStatelessProperties;
import com.baomidou.shaun.stateless.client.TokenClient;
import com.baomidou.shaun.stateless.filter.LogoutFilter;
import com.baomidou.shaun.stateless.filter.SecurityFilter;
import com.baomidou.shaun.stateless.session.NoSessionStore;
import lombok.AllArgsConstructor;
import lombok.Data;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final Authenticator<TokenCredentials> authenticator;
    private final CredentialsExtractor<TokenCredentials> credentialsExtractor;
    private final ObjectProvider<LogoutHandler> logoutHandlerProvider;
    private final ObjectProvider<List<Authorizer>> authorizerProvider;

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
        List<Authorizer> authorizerList = authorizerProvider.getIfAvailable();
        Map<String, Authorizer> authorizeMap = new HashMap<>();
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

        SecurityFilter securityFilter = new SecurityFilter();
        securityFilter.setPathMatcher(pathMatcher);
        securityFilter.setAuthorizerMap(authorizeMap);
        securityFilter.setAuthorizers(authorizers);
        securityFilter.setTokenClient(tokenClient);

        filterList.add(securityFilter);
        /* securityFilter end */

        /* logoutFilter begin */
        if (CommonHelper.isNotBlank(properties.getLogoutUrl())) {
            LogoutFilter logoutFilter = new LogoutFilter();
            logoutFilter.setPathMatcher(new OnlyPathMatcher(properties.getLogoutUrl()));
            logoutFilter.setLogoutExecutor(logoutHandlerProvider.getIfAvailable());

            filterList.add(logoutFilter);
        }

        ShaunInterceptor interceptor = new ShaunInterceptor();
        return interceptor.setSessionStore(NoSessionStore.INSTANCE).setFilterList(filterList);
    }

    @Bean
    @ConditionalOnMissingBean
    public AnnotationAspect annotationAspect() {
        return new AnnotationAspect();
    }
}
