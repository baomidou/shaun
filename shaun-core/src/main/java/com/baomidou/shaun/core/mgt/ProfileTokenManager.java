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
package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.profile.TokenProfile;
import org.pac4j.core.context.CallContext;

/**
 * profile 管理器
 *
 * @author miemie
 * @since 2020-09-04
 */
public interface ProfileTokenManager {

    /**
     * 从上下文中获取到 TokenProfile
     *
     * @param context JEEContext
     * @return TokenProfile
     */
    TokenProfile getProfile(CallContext context);

    /**
     * 把 TokenProfile 构建为 token(jwt)
     *
     * @param profile          TokenProfile
     * @param optionExpireTime 超时时间
     * @return token(jwt)
     */
    String generateToken(TokenProfile profile, String optionExpireTime);
}
