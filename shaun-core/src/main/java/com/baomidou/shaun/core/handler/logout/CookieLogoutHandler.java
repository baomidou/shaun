package com.baomidou.shaun.core.handler.logout;

import com.baomidou.shaun.core.context.cookie.CookieContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-31
 */
@Data
@AllArgsConstructor
public class CookieLogoutHandler implements LogoutHandler<CommonProfile> {

    private final CookieContext cookieContext;

    @Override
    public void logout(JEEContext context, CommonProfile profile) {
        cookieContext.clearCookie();
    }
}
