package com.baomidou.mipac4j.autoconfigure;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.function.Supplier;

import javax.servlet.DispatcherType;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.http.credentials.extractor.CookieExtractor;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import com.baomidou.mipac4j.autoconfigure.aop.AnnotationAspect;
import com.baomidou.mipac4j.autoconfigure.factory.LogoutFilterFactoryBean;
import com.baomidou.mipac4j.autoconfigure.factory.MIPac4jFilterFactoryBean;
import com.baomidou.mipac4j.autoconfigure.factory.SecurityFilterFactoryBean;
import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.client.TokenDirectClient;
import com.baomidou.mipac4j.core.client.TokenIndirectClient;
import com.baomidou.mipac4j.core.context.DefaultJ2EContextFactory;
import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.context.http.DoHttpAction;
import com.baomidou.mipac4j.core.context.session.NoSessionStore;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.extractor.TokenExtractor;
import com.baomidou.mipac4j.core.filter.LogoutFilter;
import com.baomidou.mipac4j.core.filter.MIPac4jFilter;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;
import com.baomidou.mipac4j.core.filter.SecurityFilter;
import com.baomidou.mipac4j.core.generator.DefaultJwtTokenGenerator;
import com.baomidou.mipac4j.core.generator.TokenGenerator;
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
    private final SignatureConfiguration signatureConfiguration;
    private final EncryptionConfiguration encryptionConfiguration;

    @SuppressWarnings("unchecked")
    @Bean
    public MIPac4jFilter miPac4jFilter() {
        Client client;
        CredentialsExtractor<TokenCredentials> credentialsExtractor;
        if (properties.isStateless()) {
            credentialsExtractor = new TokenExtractor(properties.getTokenLocation(), properties.getHeader(), properties.getParameter(), properties.getCookie());
        } else {
            credentialsExtractor = new CookieExtractor(properties.getCookie().getName());
        }

        Authenticator<TokenCredentials> authenticator = this.getOrDefault(Authenticator.class,
                () -> new JwtAuthenticator(signatureConfiguration, encryptionConfiguration));

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

        Matcher matcher = pathMatcher;

        SessionStore sessionStore = this.getOrDefault(SessionStore.class, () -> NoSessionStore.INSTANCE);
        J2EContextFactory j2EContextFactory = this.getOrDefault(J2EContextFactory.class, () -> DefaultJ2EContextFactory.INSTANCE);
        TokenGenerator tokenGenerator = this.getOrDefault(TokenGenerator.class,
                () -> new DefaultJwtTokenGenerator(signatureConfiguration, encryptionConfiguration)
                        .setExpireTime(properties.getExpireTime()));
        return null;
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityFilter securityFilter(Client client, Matcher matcher, SessionStore sessionStore,
                                         ProfileManagerFactory profileManagerFactory, DoHttpAction doHttpAction) throws Exception {
        SecurityFilterFactoryBean factory = new SecurityFilterFactoryBean();
        factory.setAuthorizers(properties.getAuthorizers());
        factory.setAuthorizeMap(applicationContext.getBeansOfType(Authorizer.class));
        factory.setClient(client);
        factory.setMatcher(matcher);
        factory.setSessionStore(sessionStore);
        factory.setProfileManagerFactory(profileManagerFactory);
        factory.setDoHttpAction(doHttpAction);
        return factory.getObject();
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
