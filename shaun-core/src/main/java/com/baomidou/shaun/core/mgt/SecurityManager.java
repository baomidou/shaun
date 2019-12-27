package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.enums.TokenLocation;
import com.baomidou.shaun.core.generator.TokenGenerator;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.properties.Cookie;
import com.baomidou.shaun.core.util.JEEContextFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.context.JEEContext;

/**
 * 安全管理器,封装下,统一的登录登出
 * cookie模式下登录自动设置cookie到response,登出自动清除cookie
 *
 * @author miemie
 * @since 2019-08-02
 */
@Data
@AllArgsConstructor
public class SecurityManager {

    private final TokenGenerator tokenGenerator;
    private final TokenLocation tokenLocation;
    private final LogoutHandler logoutHandler;
    private final Cookie cookie;

    /**
     * ignore
     */
    public String login(TokenProfile profile) {
        return login(profile, false, null);
    }

    /**
     * ignore
     */
    public String login(TokenProfile profile, boolean isSkipAuthenticationUser) {
        return login(profile, isSkipAuthenticationUser, null);
    }

    /**
     * 统一登录封装
     *
     * @param profile                  登录用户
     * @param isSkipAuthenticationUser 是否是跳过所有鉴权的用户(约等于是否是超管)
     * @param optionExpireTime         选择性的超时时间
     * @return token
     */
    public String login(TokenProfile profile, boolean isSkipAuthenticationUser, String optionExpireTime) {
        String token = tokenGenerator.generate(profile, isSkipAuthenticationUser, optionExpireTime);
        if (tokenLocation.enableCookie()) {
            JEEContext jeeContext = JEEContextFactory.getJEEContext();
            jeeContext.addResponseCookie(cookie.getPac4jCookie(token, tokenGenerator.getAge(optionExpireTime)));
        }
        return token;
    }

    /**
     * 用户登出,调用 LogoutHandler 进行登出
     */
    public void logout(TokenProfile profile) {
        logoutHandler.logout(profile);
    }
}
