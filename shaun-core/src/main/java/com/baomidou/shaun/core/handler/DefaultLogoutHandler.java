package com.baomidou.shaun.core.handler;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.credentials.location.Cookie;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.WebUtil;
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
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class DefaultLogoutHandler implements LogoutHandler {

    @Override
    public void logout(CoreConfig config, TokenProfile profile) {
        boolean sessionOn = config.isSessionOn();
        Cookie cookie = config.getCookie();
        JEEContext jeeContext = WebUtil.getJEEContext(sessionOn);
        if (config.getTokenLocation().enableCookie()) {
            jeeContext.addResponseCookie(cookie.getPac4jCookie("", 0));
            log.debug("logoutHandler clean cookie success!");
        }
        if (sessionOn) {
            jeeContext.addResponseCookie(cookie.getPac4jCookie("", 0));
            log.debug("logoutHandler clean cookie success!");
            boolean b = jeeContext.getSessionStore().destroySession(jeeContext);
            if (!b) {
                log.warn("LogoutHandler destroySession fail");
            }
        }
    }
}
