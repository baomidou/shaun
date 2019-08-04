package com.baomidou.shaun.core.handler;

import org.pac4j.core.profile.UserProfile;

import com.baomidou.shaun.core.mgt.SecurityManager;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 默认登出操作
 *
 * @author miemie
 * @since 2019-07-31
 */
@Data
@AllArgsConstructor
public class DefaultLogoutHandler implements LogoutHandler<UserProfile> {

    private final SecurityManager securityManager;

    @Override
    public void logout(UserProfile profile) {
        securityManager.dropUser();
    }
}
