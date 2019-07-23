package com.baomidou.mipac4j.core.context;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.CommonProfile;

import com.baomidou.mipac4j.core.generator.TokenGenerator;
import com.baomidou.mipac4j.core.properties.Cookie;
import com.baomidou.mipac4j.core.util.J2EContextUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 操作 cookie 的类
 *
 * @author miemie
 * @since 2019-07-20
 */
@Data
@AllArgsConstructor
public class CookieContext {

    private final J2EContextFactory j2EContextFactory;

    private final TokenGenerator tokenGenerator;

    private final SessionStore sessionStore;

    private final Cookie cookie;

    /**
     * 生成 token 并设置进 cookie
     *
     * @return token
     */
    public <U extends CommonProfile> String generateAndAddCookie(final U profile) {
        String token = tokenGenerator.generate(profile);
        J2EContext j2EContext = J2EContextUtil.getJ2EContext(j2EContextFactory, sessionStore);
        j2EContext.addResponseCookie(getCookie(token));
        return token;
    }

    /**
     * 清除 cookie
     */
    public void clearCookie() {
        J2EContext j2EContext = J2EContextUtil.getJ2EContext(j2EContextFactory, sessionStore);
        org.pac4j.core.context.Cookie cookie = getCookie("");
        cookie.setMaxAge(0);
        j2EContext.addResponseCookie(cookie);
    }

    private org.pac4j.core.context.Cookie getCookie(final String token) {
        org.pac4j.core.context.Cookie c = new org.pac4j.core.context.Cookie(cookie.getName(), token);
        Integer age = tokenGenerator.getAge();
        c.setVersion(cookie.getVersion());
        c.setSecure(cookie.isSecure());
        c.setPath(cookie.getPath());
        c.setMaxAge(age == null ? -1 : age);
        c.setHttpOnly(cookie.isHttpOnly());
        c.setComment(cookie.getComment());
        c.setDomain(cookie.getDomain());
        return c;
    }
}
