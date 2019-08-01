package com.baomidou.shaun.stateless.handler;

import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.stateless.cookie.CookieContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.UserProfile;

/**
 * @author miemie
 * @since 2019-07-31
 */
@Data
@AllArgsConstructor
public class CookieLogoutHandler implements LogoutHandler<UserProfile> {

    private final CookieContext cookieContext;

    @Override
    public void logout(JEEContext context, UserProfile profile) {
        cookieContext.clearCookie();
    }
}
