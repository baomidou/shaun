package com.baomidou.mipac4j.autoconfigure;

import com.baomidou.mipac4j.autoconfigure.aop.AnnotationAspect;
import com.baomidou.mipac4j.autoconfigure.factory.MIPac4jFilterFactoryBean;
import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;
import lombok.AllArgsConstructor;
import org.pac4j.core.context.session.SessionStore;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author miemie
 * @since 2019-07-18
 */
@AllArgsConstructor
@Configuration
@AutoConfigureAfter(MIPac4jAutoConfiguration.class)
public class MIPac4jSecurityAutoConfiguration {

    private final MIPac4jProperties properties;
    private final ListableBeanFactory beanFactory;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnMissingBean
    public MIPac4jFilterFactoryBean pac4jPlusFilterFactoryBean(J2EContextFactory j2EContextFactory,
                                                               SessionStore sessionStore) {
        return new MIPac4jFilterFactoryBean(beanFactory, j2EContextFactory, sessionStore);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public AnnotationAspect annotationAspect(ProfileManagerFactory profileManagerFactory, SessionStore sessionStore,
                                             J2EContextFactory j2EContextFactory) {
        return new AnnotationAspect(profileManagerFactory, sessionStore, j2EContextFactory);
    }
}
