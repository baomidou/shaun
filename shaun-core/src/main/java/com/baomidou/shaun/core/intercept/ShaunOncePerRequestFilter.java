/*
 * Copyright 2019-2021 baomidou (wonderming@vip.qq.com)
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

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.intercept.support.DoChainSupport;
import com.baomidou.shaun.core.intercept.support.ShaunFilterChain;

/**
 * @author miemie
 * @since 2019-08-08
 */
public class ShaunOncePerRequestFilter extends OncePerRequestFilter implements DoChainSupport {

    private final CoreConfig config;
    private final List<ShaunFilter> filterList;

    public ShaunOncePerRequestFilter(CoreConfig config, ShaunFilterChain filterChain) {
        this.config = config;
        this.filterList = filterChain.getOrderFilter();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        boolean result = doChain(request, response, config, filterList);
        if (result) {
            chain.doFilter(request, response);
        }
    }
}
