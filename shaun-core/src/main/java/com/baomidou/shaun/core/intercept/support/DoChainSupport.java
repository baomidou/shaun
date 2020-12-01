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
package com.baomidou.shaun.core.intercept.support;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.util.WebUtil;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author miemie
 * @since 2020-11-30
 */
public interface DoChainSupport {

    default boolean doChain(HttpServletRequest request, HttpServletResponse response, CoreConfig config, List<ShaunFilter> filterList) {
        final JEEContext context = WebUtil.getJEEContext(request, response, config.isSessionOn());
        if (!CorsUtils.isPreFlightRequest(request)) {
            for (ShaunFilter filter : filterList) {
                try {
                    HttpAction action = filter.doFilter(config, context);
                    if (action != null) {
                        config.getHttpActionHandler().handle(config, context, action);
                        return false;
                    }
                } catch (Exception e) {
                    ProfileHolder.clearProfile();
                    throw e;
                }
            }
        }
        return true;
    }
}
