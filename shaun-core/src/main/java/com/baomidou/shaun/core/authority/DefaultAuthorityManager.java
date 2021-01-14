/*
 * Copyright 2019-2021 baomidou (wonderming@vip.qq.com)
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
package com.baomidou.shaun.core.authority;

import com.baomidou.shaun.core.profile.TokenProfile;

import lombok.RequiredArgsConstructor;

/**
 * @author miemie
 * @since 2019-08-01
 */
@RequiredArgsConstructor
public class DefaultAuthorityManager implements AuthorityManager {

    private final String skipAuthenticationRolePermission;

    @Override
    public void skipAuthentication(TokenProfile profile) {
        profile.addRole(skipAuthenticationRolePermission);
    }

    @Override
    public boolean isSkipAuthentication(TokenProfile profile) {
        return profile.getRoles().contains(skipAuthenticationRolePermission);
    }
}
