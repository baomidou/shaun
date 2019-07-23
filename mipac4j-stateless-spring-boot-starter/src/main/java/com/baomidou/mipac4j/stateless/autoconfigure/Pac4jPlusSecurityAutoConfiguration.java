package com.baomidou.mipac4j.stateless.autoconfigure;

import javax.servlet.DispatcherType;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.Matcher;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.pac4jplus.annotation.AnnotationAspect;
import com.baomidou.pac4jplus.core.client.TokenClient;
import com.baomidou.pac4jplus.core.context.J2EContextFactory;
import com.baomidou.pac4jplus.core.engine.LogoutExecutor;
import com.baomidou.pac4jplus.filter.Pac4jPlusFilterFactoryBean;
import com.baomidou.pac4jplus.properties.Pac4jProperties;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-07-18
 */
@AllArgsConstructor
@Configuration
public class Pac4jPlusSecurityAutoConfiguration {

    private final J2EContextFactory j2EContextFactory;
    private final Pac4jProperties properties;
    private final ListableBeanFactory beanFactory;

    @Bean
    @ConditionalOnMissingBean
    public Pac4jPlusFilterFactoryBean pac4jPlusFilterFactoryBean(Matcher matcher, J2EContextFactory j2EContextFactory,
                                                                 TokenClient tokenClient, SessionStore sessionStore,
                                                                 LogoutExecutor logoutExecutor) {
        return new Pac4jPlusFilterFactoryBean(properties, beanFactory, matcher, j2EContextFactory, tokenClient,
                sessionStore, logoutExecutor);
    }

    @SuppressWarnings("all")
    @Bean(name = "filterPac4jFilterRegistrationBean")
    @ConditionalOnMissingBean
    protected FilterRegistrationBean filterShiroFilterRegistrationBean(Pac4jPlusFilterFactoryBean pac4jPlusFilterFactoryBean) throws Exception {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR);
        filterRegistrationBean.setFilter(pac4jPlusFilterFactoryBean.getObject());
        filterRegistrationBean.setOrder(1);

        return filterRegistrationBean;
    }

    @Bean
    public AnnotationAspect annotationAspect(Pac4jPlusFilterFactoryBean pac4jPlusFilterFactoryBean) {
        return new AnnotationAspect(pac4jPlusFilterFactoryBean.getConfig(), j2EContextFactory);
    }
}
