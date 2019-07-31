package com.baomidou.shaun.core.handler;

import com.baomidou.shaun.core.context.cookie.CookieContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-31
 */
@Data
@AllArgsConstructor
public class DefaultCookieLogoutHandler implements LogoutHandler<CommonProfile> {

    private final CookieContext cookieContext;

    @Override
    public void logout(J2EContext context, CommonProfile profile) {
        cookieContext.clearCookie();
    }
}
