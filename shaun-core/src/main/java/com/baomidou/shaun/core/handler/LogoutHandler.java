package com.baomidou.shaun.core.handler;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * 登出执行器
 *
 * @author miemie
 * @since 2019-07-22
 */
@FunctionalInterface
public interface LogoutHandler<U extends CommonProfile> {

    /**
     * 登出炒作
     *
     * @param context 上下文
     * @param profile 自己的 profile
     */
    void logout(J2EContext context, U profile);
}
