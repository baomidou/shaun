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
package com.baomidou.shaun.core.handler;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.UserProfile;

/**
 * 回调操作
 *
 * @author miemie
 * @since 2019-07-26
 */
public interface CallbackHandler {

    /**
     * callback 之后对返回获取到的 profile 转换成 TokenProfile ,
     * 再调用 SecurityManager.login 进行登陆,
     * 一般再 WebUtil.redirectUrl(context, yourUrl);
     *
     * @param context 上下文
     * @param profile callback 获取到的 profile
     */
    void callBack(JEEContext context, UserProfile profile);
}
