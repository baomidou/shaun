/*
 * Copyright 2019-2024 baomidou (wonderming@vip.qq.com)
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
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.exception.http.FoundLoginAction;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.matching.matcher.Matcher;

/**
 * logout filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Setter
@Slf4j
public class LogoutFilter extends AbstractShaunFilter {

    public LogoutFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    protected HttpAction matchThen(CoreConfig config, CallContext context) {
        if (log.isDebugEnabled()) {
            log.debug("access logout");
        }
        config.getLogoutHandler().logout(config, context, ProfileHolder.getProfile());
        return FoundLoginAction.INSTANCE;
    }

    @Override
    public int order() {
        return 300;
    }
}
