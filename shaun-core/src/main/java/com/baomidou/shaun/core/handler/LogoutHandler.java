package com.baomidou.shaun.core.handler;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * 登出执行器
 *
 * @author miemie
 * @since 2019-07-22
 */
@FunctionalInterface
public interface LogoutHandler {

    /**
     * 登出操作
     *
     * @param profile profile
     */
    void logout(TokenProfile profile);
}
