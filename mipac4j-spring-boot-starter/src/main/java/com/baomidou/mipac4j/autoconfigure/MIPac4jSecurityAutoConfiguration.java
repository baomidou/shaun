package com.baomidou.mipac4j.autoconfigure;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.DispatcherType;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.core.util.CommonHelper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.baomidou.mipac4j.autoconfigure.aop.AnnotationAspect;
import com.baomidou.mipac4j.autoconfigure.factory.LogoutFilterFactoryBean;
import com.baomidou.mipac4j.autoconfigure.factory.MIPac4jFilterFactoryBean;
import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.client.TokenDirectClient;
import com.baomidou.mipac4j.core.client.TokenIndirectClient;
import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.context.http.DoHttpAction;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.filter.LogoutFilter;
import com.baomidou.mipac4j.core.filter.MIPac4jFilter;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;
import com.baomidou.mipac4j.core.filter.SecurityFilter;
import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-07-18
 */
@AllArgsConstructor
@Configuration
@AutoConfigureAfter(MIPac4jAutoConfiguration.class)
public class MIPac4jSecurityAutoConfiguration {

    private final MIPac4jProperties properties;
    private final ApplicationContext applicationContext;
    private final Authenticator<TokenCredentials> authenticator;
    private final CredentialsExtractor<TokenCredentials> credentialsExtractor;
    private final SessionStore sessionStore;
    private final ProfileManagerFactory profileManagerFactory;
    private final J2EContextFactory j2EContextFactory;
    private final DoHttpAction doHttpAction;
    private final LogoutExecutor logoutExecutor;

    @Bean
    public MIPac4jFilter miPac4jFilter() {
        Client client;

        if (properties.isStateless()) {
            client = new TokenDirectClient(credentialsExtractor, authenticator);
        } else {
            client = new TokenIndirectClient(properties.getLoginUrl(), credentialsExtractor, authenticator);
        }

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
        Clients clients = new Clients();
        clients.setClients(client);
        clients.setDefaultSecurityClients(client.getName());
        Config securityConfig = new Config(clients);

        SecurityFilter securityFilter = new SecurityFilter();
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

        securityConfig.setSessionStore(sessionStore);
        securityConfig.setProfileManagerFactory(profileManagerFactory);
        securityConfig.addMatcher(Pac4jConstants.MATCHERS, pathMatcher);

        securityFilter.setConfig(securityConfig);
        securityFilter.setAuthorizers(authorizers);
        securityFilter.setMarchers(Pac4jConstants.MATCHERS);
        securityFilter.setDoHttpAction(doHttpAction);

        filterList.add(securityFilter);
        /* securityFilter end */

        /* logoutFilter begin */
        if (CommonHelper.isNotBlank(properties.getLogoutUrl())) {
            Clients logoutClients = new Clients();
            clients.setClients(client);
            clients.setDefaultSecurityClients(client.getName());

            Config logoutConfig = new Config(logoutClients);
            logoutConfig.setSessionStore(sessionStore);
            logoutConfig.setProfileManagerFactory(profileManagerFactory);

            LogoutFilter logoutFilter = new LogoutFilter();
            logoutFilter.setConfig(logoutConfig);
            logoutFilter.setLogoutUrl(properties.getLogoutUrl());
            logoutFilter.setLogoutExecutor(logoutExecutor);
            logoutFilter.setOutThenUrl(properties.getLoginUrl());

            filterList.add(logoutFilter);
        }
        /* logoutFilter end */

        /* threeLandingFilter begin */
        // todo
        /* threeLandingFilter end */

        /* callbackFilter begin */
        // todo
        /* callbackFilter end */

        MIPac4jFilter filter = new MIPac4jFilter();
        filter.setFilterList(filterList);
        filter.setSessionStore(sessionStore);
        filter.setJ2EContextFactory(j2EContextFactory);
        return filter;
    }

    @Bean
    @ConditionalOnMissingBean
    public LogoutFilter logoutFilter(Client client, SessionStore sessionStore, LogoutExecutor logoutExecutor,
                                     ProfileManagerFactory profileManagerFactory)
            throws Exception {
        LogoutFilterFactoryBean factory = new LogoutFilterFactoryBean();
        factory.setClient(client);
        factory.setLogoutExecutor(logoutExecutor);
        factory.setProfileManagerFactory(profileManagerFactory);
        factory.setProperties(properties);
        factory.setSessionStore(sessionStore);
        return factory.getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    public MIPac4jFilterFactoryBean pac4jPlusFilterFactoryBean(J2EContextFactory j2EContextFactory,
                                                               SessionStore sessionStore) {
        MIPac4jFilterFactoryBean factory = new MIPac4jFilterFactoryBean(j2EContextFactory, sessionStore);
        factory.setPac4jFilters(new ArrayList<>(applicationContext.getBeansOfType(Pac4jFilter.class).values()));
        return factory;
    }

    @SuppressWarnings("all")
    @Bean(name = "mIPac4jFilterRegistrationBean")
    @ConditionalOnMissingBean
    protected FilterRegistrationBean<MIPac4jFilter> mIPac4jFilterRegistrationBean(MIPac4jFilterFactoryBean miPac4jFilterFactoryBean) throws Exception {
        FilterRegistrationBean<MIPac4jFilter> filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        filterRegistrationBean.setFilter(miPac4jFilterFactoryBean.getObject());
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public AnnotationAspect annotationAspect(ProfileManagerFactory profileManagerFactory, SessionStore sessionStore,
                                             J2EContextFactory j2EContextFactory) {
        return new AnnotationAspect(profileManagerFactory, sessionStore, j2EContextFactory);
    }

    private <T> T getOrDefault(Class<T> clazz, Supplier<T> supplier) {
        if (applicationContext.getBeanNamesForType(clazz, false, false).length > 0) {
            return applicationContext.getBean(clazz);
        }
        return supplier.get();
    }
}
