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
package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.exception.http.FoundLoginAction;
import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.profile.TokenProfile;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;

/**
 * logout filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
@Slf4j
@RequiredArgsConstructor
public class LogoutFilter implements ShaunFilter {

    private final Matcher pathMatcher;
    private SecurityManager securityManager;

    @Override
    public HttpAction doFilter(CoreConfig config, JEEContext context) {
        if (pathMatcher.matches(context)) {
            if (log.isDebugEnabled()) {
                log.debug("access logout");
            }
            final TokenProfile profile = ProfileHolder.getProfile();
            securityManager.logout(profile);
            return FoundLoginAction.INSTANCE;
        }
        return null;
    }

    @Override
    public int order() {
        return 300;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        CommonHelper.assertNotNull("securityManager", securityManager);
    }
}
