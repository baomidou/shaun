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
package com.baomidou.shaun.core.context;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * @author miemie
 * @since 2020-05-25
 */
public interface ProfileHolderStrategy {

    /**
     * Clears the current TokenProfile.
     */
    void clearProfile();

    /**
     * Obtains the current TokenProfile.
     */
    TokenProfile getProfile();

    /**
     * Sets the current TokenProfile.
     */
    void setProfile(TokenProfile profile);
}
