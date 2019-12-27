package com.baomidou.shaun.core.handler;

import com.baomidou.shaun.core.profile.TokenProfile;
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
     * callback 之后对返回获取到的 profile 转换成自己的 profile
     *
     * @param context 上下文
     * @param profile callback 获取到的 profile
     * @return profile
     */
    TokenProfile callBack(JEEContext context, UserProfile profile);
}
