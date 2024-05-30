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
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.HttpActionInstance;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.jee.context.JEEContext;

/**
 * security filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Slf4j
public class SecurityFilter extends AbstractShaunFilter {

    public SecurityFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    protected HttpAction matchThen(CoreConfig config, JEEContext context) {
        if (log.isDebugEnabled()) {
            log.debug("access security for path : \"{}\" -> \"{}\"", context.getPath(), context.getRequestMethod());
        }
        TokenProfile profile = config.getProfileTokenManager().getProfile(context);
        if (profile == null) {
            return HttpActionInstance.UNAUTHORIZED;
        }
        if (!config.getProfileStateManager().isOnline(profile)) {
            return HttpActionInstance.UNAUTHORIZED;
        }
        if (!config.authorizationChecker(context, profile)) {
            return HttpActionInstance.FORBIDDEN;
        }
        ProfileHolder.setProfile(profile);
        if (log.isDebugEnabled()) {
            log.debug("authenticated and authorized -> grant access");
        }
        return null;
    }

    @Override
    public int order() {
        return 200;
    }
}
