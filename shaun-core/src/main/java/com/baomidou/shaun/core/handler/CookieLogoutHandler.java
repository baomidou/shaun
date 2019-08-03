package com.baomidou.shaun.core.handler;

import org.pac4j.core.profile.UserProfile;

import com.baomidou.shaun.core.cookie.CookieContext;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author miemie
 * @since 2019-07-31
 */
@Data
@AllArgsConstructor
public class CookieLogoutHandler implements LogoutHandler<UserProfile> {

    private final CookieContext cookieContext;

    @Override
    public void logout(UserProfile profile) {
        cookieContext.clearCookie();
    }
}
