package com.baomidou.shaun.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.models.ShaunInterceptor;
import com.baomidou.shaun.core.models.ShaunRequestFilter;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author miemie
 * @since 2019-08-08
 */
@Data
@AllArgsConstructor
@Configuration
@AutoConfigureAfter(ShaunFilterAutoConfiguration.class)
public class ShaunModelAutoConfiguration {

    private final List<ShaunFilter> filters;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "interceptor", matchIfMissing = true)
    public ShaunInterceptor shaunInterceptor() {
        ShaunInterceptor interceptor = new ShaunInterceptor();
        return interceptor.setFilterList(filters);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "web_filter")
    public ShaunRequestFilter shaunOncePerRequestFilter() {
        ShaunRequestFilter oncePerRequestFilter = new ShaunRequestFilter();
        oncePerRequestFilter.setFilterList(filters);
        return oncePerRequestFilter;
    }

    @Configuration
    @AutoConfigureAfter(ShaunModelAutoConfiguration.class)
    @ConditionalOnBean(ShaunInterceptor.class)
    @AllArgsConstructor
    public static class ShaunWebMvcConfiguration implements WebMvcConfigurer {

        private final ShaunInterceptor interceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(interceptor).addPathPatterns("/**");
        }
    }
}
