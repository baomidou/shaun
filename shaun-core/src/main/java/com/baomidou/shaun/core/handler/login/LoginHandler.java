package com.baomidou.shaun.core.handler.login;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;

/**
 * 拦截到登陆验证请求的 url,在这里进行操作
 *
 * @author miemie
 * @since 2019-07-26
 */
public interface LoginHandler<R extends CommonProfile, C extends Credentials> {

    /**
     * @param context     上下文
     * @param credentials 凭证
     * @return 自己的 profile
     */
    R login(J2EContext context, C credentials);
}
