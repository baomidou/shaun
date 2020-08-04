package com.baomidou.shaun.autoconfigure;

import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.models.ShaunInterceptor;
import com.baomidou.shaun.core.models.ShaunRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author miemie
 * @since 2020-08-04
 */
@Configuration(proxyBeanMethods = false)
public class ShaunWebSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(HandlerInterceptor.class)
    @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "interceptor", matchIfMissing = true)
    public ShaunInterceptor shaunInterceptor(Config config, List<ShaunFilter> filters) {
        ShaunInterceptor interceptor = new ShaunInterceptor();
        return interceptor.setFilterList(filters).setConfig(config);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(OncePerRequestFilter.class)
    @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "web_filter")
    public ShaunRequestFilter shaunOncePerRequestFilter(Config config, List<ShaunFilter> filters) {
        ShaunRequestFilter oncePerRequestFilter = new ShaunRequestFilter();
        return oncePerRequestFilter.setFilterList(filters).setConfig(config);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass({HandlerInterceptor.class, WebMvcConfigurer.class})
    @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "interceptor", matchIfMissing = true)
    static class ShaunWebMvcConfiguration {

        @RequiredArgsConstructor
        @Configuration(proxyBeanMethods = false)
        public static class ShaunWebConfiguration implements WebMvcConfigurer {

            private final ShaunInterceptor shaunInterceptor;

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(shaunInterceptor).addPathPatterns("/**");
            }
        }
    }
}
