package com.baomidou.shaun.autoconfigure;

import com.baomidou.shaun.autoconfigure.intercept.MethodSecurityAdvisor;
import com.baomidou.shaun.autoconfigure.intercept.MethodSecurityInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author miemie
 * @since 2020-08-04
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "shaun.annotations.enabled", havingValue = "true", matchIfMissing = true)
public class ShaunAnnotationAutoConfiguration {

    @Bean
    public MethodSecurityInterceptor shaunMethodSecurityInterceptor() {
        return new MethodSecurityInterceptor();
    }

    @Bean
    public MethodSecurityAdvisor shaunMethodSecurityAdvisor(MethodSecurityInterceptor interceptor) {
        MethodSecurityAdvisor advisor = new MethodSecurityAdvisor();
        advisor.setAdvice(interceptor);
        return advisor;
    }
}
