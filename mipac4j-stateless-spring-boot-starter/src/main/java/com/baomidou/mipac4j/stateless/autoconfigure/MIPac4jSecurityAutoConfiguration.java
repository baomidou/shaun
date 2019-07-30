package com.baomidou.mipac4j.stateless.autoconfigure;

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
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.baomidou.mipac4j.core.client.TokenClient;
import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;
import com.baomidou.mipac4j.core.filter.stateless.StatelessLogoutFilter;
import com.baomidou.mipac4j.core.filter.stateless.StatelessSecurityFilter;
import com.baomidou.mipac4j.core.interceptor.MIPac4jInterceptor;
import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;
import com.baomidou.mipac4j.stateless.autoconfigure.aop.AnnotationAspect;
import com.baomidou.mipac4j.stateless.autoconfigure.properties.MIPac4jProperties;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-07-18
 */
@AllArgsConstructor
@Configuration
@AutoConfigureAfter(MIPac4jAutoConfiguration.class)
public class MIPac4jSecurityAutoConfiguration implements WebMvcConfigurer {

    private final MIPac4jProperties properties;
    private final ApplicationContext applicationContext;
    private final Authenticator<TokenCredentials> authenticator;
    private final CredentialsExtractor<TokenCredentials> credentialsExtractor;
    private final J2EContextFactory j2EContextFactory;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(miPac4jInterceptor()).addPathPatterns("/**");
    }

    @Bean
    @ConditionalOnMissingBean
    public MIPac4jInterceptor miPac4jInterceptor() {
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

        List<Pac4jFilter> filterList = new ArrayList<>();

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
            LogoutExecutor logoutExecutor = this.getOrDefault(LogoutExecutor.class, () -> LogoutExecutor.DO_NOTHING);
            logoutFilter.setLogoutExecutor(logoutExecutor);

            filterList.add(logoutFilter);
        }

        MIPac4jInterceptor interceptor = new MIPac4jInterceptor();
        return interceptor.setJ2EContextFactory(j2EContextFactory).setFilterList(filterList);
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
