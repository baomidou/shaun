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
package com.baomidou.shaun.core.handler;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.CallContext;

/**
 * 默认登出操作
 *
 * @author miemie
 * @since 2019-07-31
 */
@Slf4j
public class DefaultLogoutHandler implements LogoutHandler {

    @Override
    public void logout(CoreConfig config, CallContext context, TokenProfile profile) {
        if (config.getTokenLocation().enableCookie()) {
            config.getCookie().clean(context);
            log.debug("logoutHandler clean cookie success!");
        }
        context.sessionStore().destroySession(context.webContext());
        config.getProfileStateManager().offline(profile);
        ProfileHolder.clearProfile();
    }
}
