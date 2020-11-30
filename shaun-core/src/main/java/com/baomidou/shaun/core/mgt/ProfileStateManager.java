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
package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * profile 状态管理器
 *
 * @author miemie
 * @since 2020-09-04
 */
public interface ProfileStateManager {

    /**
     * login 后置操作
     * <p>
     * 可以在这里把用户信息存储进外部(比如redis)
     *
     * @param profile 登陆用户
     */
    default void online(TokenProfile profile) {
        // do nothing
    }

    /**
     * 访问需要登录的资源之前进行验证是否允许访问
     * 只适合判断该用户的登录信息是否有效
     * <p>
     * 可以在这里从外部(比如redis)读取用户判断是否允许访问
     *
     * @param profile 登陆用户
     * @return 是否允许访问
     */
    default boolean isOnline(TokenProfile profile) {
        return true;
    }

    /**
     * logout 后置操作
     * <p>
     * 可以在这里把用户信息从外部存储(比如redis)上删除
     *
     * @param profile 登陆用户
     */
    default void offline(TokenProfile profile) {
        // do nothing
    }
}
