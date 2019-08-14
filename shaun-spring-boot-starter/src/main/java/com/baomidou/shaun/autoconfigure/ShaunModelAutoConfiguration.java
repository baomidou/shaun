package com.baomidou.shaun.autoconfigure;

import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.models.ShaunInterceptor;
import com.baomidou.shaun.core.models.ShaunOncePerRequestFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

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
    public ShaunOncePerRequestFilter shaunOncePerRequestFilter() {
        ShaunOncePerRequestFilter oncePerRequestFilter = new ShaunOncePerRequestFilter();
        oncePerRequestFilter.setFilterList(filters);
        return oncePerRequestFilter;
    }

    @Configuration
    @AutoConfigureAfter(ShaunModelAutoConfiguration.class)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(WebMvcConfigurer.class)
    @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "interceptor", matchIfMissing = true)
    public static class ShaunWebConfiguration {

        @Configuration
        @AllArgsConstructor
        public static class ShaunWebMvcConfiguration implements WebMvcConfigurer {

            private final ShaunInterceptor shaunInterceptor;

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(shaunInterceptor).addPathPatterns("/**");
            }
        }
    }
}
