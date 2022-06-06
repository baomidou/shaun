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
package com.baomidou.shaun.core.context;

import com.baomidou.shaun.core.profile.TokenProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author miemie
 * @since 2020-05-25
 */
final class RequestProfileHolderStrategy implements ProfileHolderStrategy {

    private static final String key_profile = Pac4jConstants.USER_PROFILES;

    @Override
    public void clearProfile() {
        // do nothing
    }

    @Override
    public TokenProfile getProfile() {
        return (TokenProfile) request().getAttribute(key_profile);
    }

    @Override
    public void setProfile(TokenProfile profile) {
        Assert.notNull(profile, "Only non-null TokenProfile instances are permitted");
        request().setAttribute(key_profile, profile);
    }

    @SuppressWarnings("all")
    private HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
}
