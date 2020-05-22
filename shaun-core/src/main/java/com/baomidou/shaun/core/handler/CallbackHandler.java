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
     * 一般再 JEEContextUtil.redirectUrl(context, yourUrl);
     *
     * @param context 上下文
     * @param profile callback 获取到的 profile
     */
    void callBack(JEEContext context, UserProfile profile);
}
