package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.config.ShaunConfig;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.ExpireTimeUtil;
import com.baomidou.shaun.core.util.WebUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.util.CommonHelper;

/**
 * 安全管理器,封装下,统一的登录登出
 * cookie模式下登录自动设置cookie到response,登出自动清除cookie
 *
 * @author miemie
 * @since 2019-08-02
 */
@Data
@RequiredArgsConstructor
public class SecurityManager {

    private final ShaunConfig config;

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
        if (isSkipAuthenticationUser) {
            config.getAuthorityManager().skipAuthentication(profile);
        }
        String expireTime = chooseExpireTime(optionExpireTime);
        String token = config.getProfileTokenManager().generateToken(profile, expireTime);
        profile.setToken(token);
        if (config.getTokenLocation().enableCookie()) {
            JEEContext jeeContext = WebUtil.getJEEContext(config.isSessionOn());
            jeeContext.addResponseCookie(config.getCookie().getPac4jCookie(token, getCookieAge(expireTime)));
        }
        config.getProfileStateManager().online(profile);
        return token;
    }

    /**
     * 用户登出
     */
    public void logout(TokenProfile profile) {
        ProfileHolder.clearProfile();
        config.getLogoutHandler().logout(config, profile);
        config.getProfileStateManager().offline(profile);
    }

    private String chooseExpireTime(String optionExpireTime) {
        if (CommonHelper.isNotBlank(optionExpireTime)) {
            return optionExpireTime;
        }
        return config.getExpireTime();
    }

    /**
     * 默认提前1秒到期
     */
    private int getCookieAge(String expireTime) {
        if (CommonHelper.isNotBlank(expireTime)) {
            return ExpireTimeUtil.getTargetSecond(expireTime) - 1;
        }
        return -1;
    }
}
