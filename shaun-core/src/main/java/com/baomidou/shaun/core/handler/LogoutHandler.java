package com.baomidou.shaun.core.handler;

import org.pac4j.core.profile.UserProfile;

/**
 * 登出执行器
 *
 * @author miemie
 * @since 2019-07-22
 */
@FunctionalInterface
public interface LogoutHandler<U extends UserProfile> {

    /**
     * 登出操作
     *
     * @param profile 自己的 profile
     */
    void logout(U profile);
}
