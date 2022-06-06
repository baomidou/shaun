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
package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.CoreConfig;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;

/**
 * @author miemie
 * @since 2020-12-01
 */
public abstract class AbstractShaunFilter implements ShaunFilter {

    private final Matcher pathMatcher;

    public AbstractShaunFilter(Matcher pathMatcher) {
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        this.pathMatcher = pathMatcher;
    }

    @Override
    final public HttpAction doFilter(CoreConfig config, JEEContext context) {
        if (pathMatcher.matches(context)) {
            try {
                return matchThen(config, context);
            } catch (Exception ex) {
                return handleEx(ex);
            }
        }
        return null;
    }

    protected abstract HttpAction matchThen(CoreConfig config, JEEContext context);

    protected HttpAction handleEx(Exception e) {
        if (e instanceof HttpAction) {
            return (HttpAction) e;
        }
        throw new RuntimeException(e);
    }
}
