package com.baomidou.shaun.core.handler;

import com.baomidou.shaun.core.context.Cookie;
import com.baomidou.shaun.core.enums.TokenLocation;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.JEEContextFactory;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.JEEContext;

/**
 * 默认登出操作
 *
 * @author miemie
 * @since 2019-07-31
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class DefaultLogoutHandler implements LogoutHandler {

    private final TokenLocation tokenLocation;
    private final Cookie cookie;

    @Override
    public void logout(TokenProfile profile) {
        if (tokenLocation.enableCookie()) {
            JEEContext jeeContext = JEEContextFactory.getJEEContext();
            jeeContext.addResponseCookie(cookie.getPac4jCookie("", 0));
            log.debug("logoutHandler clean cookie success!");
        }
    }
}
