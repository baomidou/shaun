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
package com.baomidou.shaun.togglz;

import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

import java.util.function.Function;

/**
 * A {@link UserProvider} implementation for Shaun.
 * 注意: 只适用于 web_filter 的拦截方式
 *
 * @author miemie
 * @since 2021-01-18
 */
public class ShaunUserProvider implements UserProvider {

    private final CoreConfig config;
    private final String featureAdminRole;
    private final Function<TokenProfile, String> featureUserNameSupplier;

    public ShaunUserProvider(CoreConfig config, String featureAdminRole) {
        this(config, featureAdminRole, TokenProfile::getUsername);
    }

    public ShaunUserProvider(CoreConfig config, String featureAdminRole, Function<TokenProfile, String> featureUserNameSupplier) {
        this.config = config;
        this.featureAdminRole = featureAdminRole;
        this.featureUserNameSupplier = featureUserNameSupplier;
    }

    @Override
    public FeatureUser getCurrentUser() {
        TokenProfile profile = ProfileHolder.getProfile();
        if (profile != null) {
            AuthorityManager manager = config.getAuthorityManager();
            boolean featureAdmin = manager.isSkipAuthentication(profile) || manager.roles(profile).contains(featureAdminRole);
            return new SimpleFeatureUser(featureUserNameSupplier.apply(profile), featureAdmin);
        }
        // user is not authenticated
        return null;
    }
}
