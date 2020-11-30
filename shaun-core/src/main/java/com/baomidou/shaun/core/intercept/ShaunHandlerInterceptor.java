/*
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
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
package com.baomidou.shaun.core.intercept;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.intercept.support.DoChainSupport;
import com.baomidou.shaun.core.intercept.support.ShaunFilterChain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author miemie
 * @since 2019-07-29
 */
@Data
@Accessors(chain = true)
public class ShaunHandlerInterceptor implements HandlerInterceptor, DoChainSupport {

    private final List<ShaunFilter> filterList;
    private final CoreConfig config;

    public ShaunHandlerInterceptor(CoreConfig config, ShaunFilterChain filterChain) {
        this.config = config;
        this.filterList = filterChain.getOrderFilter();
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        return doChain(request, response, config, filterList);
    }
}
