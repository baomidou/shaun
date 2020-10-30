package com.baomidou.shaun.core.handler.stateful;

import com.baomidou.shaun.core.context.Cookie;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.WebUtil;
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
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class DefaultStatefulLogoutHandler implements LogoutHandler {

    private final Cookie cookie;

    @Override
    public void logout(TokenProfile profile) {
        JEEContext jeeContext = WebUtil.getJEEContext();
        jeeContext.addResponseCookie(cookie.getPac4jCookie("", 0));
        log.debug("logoutHandler clean cookie success!");
        boolean b = jeeContext.getSessionStore().destroySession(jeeContext);
        if (!b) {
            log.warn("LogoutHandler destroySession fail");
        }
    }
}
