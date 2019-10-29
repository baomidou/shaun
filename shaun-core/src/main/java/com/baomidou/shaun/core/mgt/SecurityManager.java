package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.enums.TokenLocation;
import com.baomidou.shaun.core.generator.TokenGenerator;
import com.baomidou.shaun.core.properties.Cookie;
import com.baomidou.shaun.core.util.JEEContextFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;

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
    private final Cookie cookie;

    /**
     * 统一登录封装,
     * 默认不是管理员
     *
     * @param profile 登录用户
     * @param <U>     泛型
     * @return token
     */
    public <U extends CommonProfile> String login(U profile) {
        return login(profile, false);
    }

    /**
     * 统一登录封装
     *
     * @param profile 登录用户
     * @param isAdmin 是否管理员
     * @param <U>     泛型
     * @return token
     */
    public <U extends CommonProfile> String login(U profile, boolean isAdmin) {
        String token = tokenGenerator.generate(profile, isAdmin);
        if (tokenLocation == TokenLocation.COOKIE) {
            JEEContext jeeContext = JEEContextFactory.getJEEContext();
            jeeContext.addResponseCookie(getCookie(token, tokenGenerator.getAge()));
        }
        return token;
    }

    /**
     * 移除用户
     * <p>
     * 只在 cookie 存 token 模式下生效
     *
     * @return 是否成功
     */
    public boolean dropUser() {
        if (tokenLocation == TokenLocation.COOKIE) {
            JEEContext jeeContext = JEEContextFactory.getJEEContext();
            jeeContext.addResponseCookie(getCookie("", 0));
            return true;
        }
        return false;
    }

    private org.pac4j.core.context.Cookie getCookie(final String token, Integer maxAge) {
        org.pac4j.core.context.Cookie c = new org.pac4j.core.context.Cookie(cookie.getName(), token);
        c.setVersion(cookie.getVersion());
        c.setSecure(cookie.isSecure());
        c.setPath(cookie.getPath());
        c.setMaxAge(maxAge == null ? -1 : maxAge);
        c.setHttpOnly(cookie.isHttpOnly());
        c.setComment(cookie.getComment());
        c.setDomain(cookie.getDomain());
        return c;
    }
}
