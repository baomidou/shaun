/*
 * Copyright 2019-2022 baomidou (wonderming@vip.qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.autoconfigure;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.intercept.ShaunHandlerInterceptor;
import com.baomidou.shaun.core.intercept.ShaunOncePerRequestFilter;
import com.baomidou.shaun.core.intercept.support.ShaunFilterChain;
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

/**
 * @author miemie
 * @since 2020-08-04
 */
@Configuration(proxyBeanMethods = false)
public class ShaunWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(HandlerInterceptor.class)
    @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "interceptor", matchIfMissing = true)
    public ShaunHandlerInterceptor shaunHandlerInterceptor(CoreConfig coreConfig, ShaunFilterChain shaunFilterChain) {
        return new ShaunHandlerInterceptor(coreConfig, shaunFilterChain);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(OncePerRequestFilter.class)
    @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "web_filter")
    public ShaunOncePerRequestFilter shaunOncePerRequestFilter(CoreConfig coreConfig, ShaunFilterChain shaunFilterChain) {
        return new ShaunOncePerRequestFilter(coreConfig, shaunFilterChain);
    }

    @RequiredArgsConstructor
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnProperty(prefix = "shaun", name = "model", havingValue = "interceptor", matchIfMissing = true)
    public static class ShaunWebMvcConfigurer implements WebMvcConfigurer {

        private final ShaunHandlerInterceptor shaunHandlerInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(shaunHandlerInterceptor).addPathPatterns("/**");
        }
    }
}
