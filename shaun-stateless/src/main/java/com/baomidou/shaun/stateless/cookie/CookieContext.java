package com.baomidou.shaun.stateless.cookie;

import com.baomidou.shaun.core.util.JEEContextFactory;
import com.baomidou.shaun.stateless.generator.TokenGenerator;
import com.baomidou.shaun.stateless.properties.Cookie;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * 操作 cookie 的类
 *
 * @author miemie
 * @since 2019-07-20
 */
@Data
@AllArgsConstructor
public class CookieContext {

    private final TokenGenerator tokenGenerator;
    private final Cookie cookie;

    /**
     * 生成 token 并设置进 cookie
     *
     * @return token
     */
    public <U extends CommonProfile> String generateAndAddCookie(final U profile) {
        String token = tokenGenerator.generate(profile);
        JEEContext jeeContext = JEEContextFactory.getJEEContext();
        jeeContext.addResponseCookie(getCookie(token));
        return token;
    }

    /**
     * 清除 cookie
     */
    public void clearCookie() {
        JEEContext jeeContext = JEEContextFactory.getJEEContext();
        org.pac4j.core.context.Cookie cookie = getCookie("");
        cookie.setMaxAge(0);
        jeeContext.addResponseCookie(cookie);
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
