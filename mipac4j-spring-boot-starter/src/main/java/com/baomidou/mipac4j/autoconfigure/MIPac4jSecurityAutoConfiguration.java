package com.baomidou.mipac4j.autoconfigure;

import com.baomidou.mipac4j.autoconfigure.aop.AnnotationAspect;
import com.baomidou.mipac4j.autoconfigure.factory.LogoutFilterFactoryBean;
import com.baomidou.mipac4j.autoconfigure.factory.MIPac4jFilterFactoryBean;
import com.baomidou.mipac4j.autoconfigure.factory.SecurityFilterFactoryBean;
import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.context.http.DoHttpAction;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.filter.LogoutFilter;
import com.baomidou.mipac4j.core.filter.MIPac4jFilter;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;
import com.baomidou.mipac4j.core.filter.SecurityFilter;
import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;
import lombok.AllArgsConstructor;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.Matcher;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * @author miemie
 * @since 2019-07-18
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@AllArgsConstructor
@Configuration
@AutoConfigureAfter(MIPac4jAutoConfiguration.class)
public class MIPac4jSecurityAutoConfiguration {

    private final MIPac4jProperties properties;
    private final ApplicationContext applicationContext;

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
}
