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

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.intercept.support.ShaunFilterChain;
import com.baomidou.shaun.core.util.WebUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.BadRequestAction;
import org.springframework.lang.NonNull;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * @author miemie
 * @since 2019-07-29
 */
@Data
@Accessors(chain = true)
public class ShaunHandlerInterceptor implements HandlerInterceptor {

    private final List<ShaunFilter> filterList;
    private final CoreConfig config;

    public ShaunHandlerInterceptor(CoreConfig config, ShaunFilterChain filterChain) {
        this.config = config;
        this.filterList = filterChain.getOrderFilter();
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        final JEEContext context = WebUtil.getJEEContext(request, response, config.isSessionOn());
        if (config.getMatchingChecker().matches(context, config.getMatcherNames(), config.getMatchersMap(), Collections.emptyList())) {
            if (!CorsUtils.isPreFlightRequest(request)) {
                for (ShaunFilter filter : filterList) {
                    try {
                        if (!filter.goOnChain(config, context)) {
                            return false;
                        }
                    } catch (Exception e) {
                        ProfileHolder.clearProfile();
                        throw e;
                    }
                }
            }
        } else {
            config.getHttpActionHandler().preHandle(BadRequestAction.INSTANCE, context);
        }
        return true;
    }
}
