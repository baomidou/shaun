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
package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;

import java.util.Collections;

/**
 * security filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class SecurityFilter implements ShaunFilter {

    private final Matcher pathMatcher;

    @Override
    public boolean goOnChain(CoreConfig config, JEEContext context) {
        if (pathMatcher.matches(context)) {
            if (log.isDebugEnabled()) {
                log.debug("access security for path : \"{}\" -> \"{}\"", context.getPath(), context.getRequestMethod());
            }
            TokenProfile profile = config.getProfileTokenManager().getProfile(context);
            if (profile != null) {
                if (config.getProfileStateManager().isOnline(profile) &&
                        config.getAuthorizationChecker().isAuthorized(context, Collections.singletonList(profile),
                                config.getAuthorizerNames(), config.getAuthorizersMap(), Collections.emptyList())) {
                    ProfileHolder.setProfile(profile);
                    if (log.isDebugEnabled()) {
                        log.debug("authenticated and authorized -> grant access");
                    }
                    return true;
                }
            }
            this.fail(config, context);
            return false;
        }
        return true;
    }

    protected void fail(CoreConfig config, JEEContext context) {
        if (config.isStateless()) {
            config.getHttpActionHandler().preHandle(UnauthorizedAction.INSTANCE, context);
            return;
        }
        if (config.getAjaxRequestResolver().isAjax(context)) {
            config.getHttpActionHandler().preHandle(UnauthorizedAction.INSTANCE, context);
        } else {
            config.redirectLoginUrl(context);
        }
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
    }
}
