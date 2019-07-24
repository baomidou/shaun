package com.baomidou.mipac4j.autoconfigure;

import javax.servlet.DispatcherType;

import org.pac4j.core.client.Client;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.Matcher;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mipac4j.autoconfigure.aop.AnnotationAspect;
import com.baomidou.mipac4j.autoconfigure.filter.MIPac4jFilterFactoryBean;
import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-07-18
 */
@AllArgsConstructor
@Configuration
@AutoConfigureAfter(MIPac4jAutoConfiguration.class)
public class MIPac4jSecurityAutoConfiguration {

    private final J2EContextFactory j2EContextFactory;
    private final MIPac4jProperties properties;
    private final ListableBeanFactory beanFactory;

    @Bean
    @ConditionalOnMissingBean
    public MIPac4jFilterFactoryBean pac4jPlusFilterFactoryBean(Matcher matcher, J2EContextFactory j2EContextFactory,
                                                               Client client, SessionStore sessionStore,
                                                               LogoutExecutor logoutExecutor) {
        return new MIPac4jFilterFactoryBean(properties, beanFactory, matcher, j2EContextFactory, client,
                sessionStore, logoutExecutor);
    }

    @SuppressWarnings("all")
    @Bean(name = "filterPac4jFilterRegistrationBean")
    @ConditionalOnMissingBean
    protected FilterRegistrationBean filterShiroFilterRegistrationBean(MIPac4jFilterFactoryBean pac4jPlusFilterFactoryBean) throws Exception {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR);
        filterRegistrationBean.setFilter(pac4jPlusFilterFactoryBean.getObject());
        filterRegistrationBean.setOrder(1);

        return filterRegistrationBean;
    }

    @Bean
    public AnnotationAspect annotationAspect(MIPac4jFilterFactoryBean pac4jPlusFilterFactoryBean) {
        return new AnnotationAspect(pac4jPlusFilterFactoryBean.getConfig(), j2EContextFactory);
    }
}
