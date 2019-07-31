package com.baomidou.shaun.core.handler.callback;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-26
 */
public interface CallbackHandler<R extends CommonProfile> {

    /**
     * callback 之后对返回获取到的 profile 转换成自己的 profile
     *
     * @param context 上下文
     * @param profile callback 获取到的 profile
     * @return 自己的 profile
     */
    R callBack(WebContext context, CommonProfile profile);
}
