/**
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
