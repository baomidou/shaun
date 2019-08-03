package com.baomidou.shaun.core.mgt;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;

import com.baomidou.shaun.core.context.GlobalConfig;
import com.baomidou.shaun.core.generator.TokenGenerator;
import com.baomidou.shaun.core.properties.Cookie;
import com.baomidou.shaun.core.util.JEEContextFactory;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author miemie
 * @since 2019-08-02
 */
@Data
@AllArgsConstructor
public class SecurityManager {

    private final TokenGenerator tokenGenerator;
    private final Cookie cookie;

    public <U extends CommonProfile> String login(U profile) {
        String token = tokenGenerator.generate(profile);
        if (!GlobalConfig.isStateless()) {
            JEEContext jeeContext = JEEContextFactory.getJEEContext();
            jeeContext.addResponseCookie(getCookie(token));
        }
        return token;
    }

    public boolean dropUser() {
        if (!GlobalConfig.isStateless()) {
            JEEContext jeeContext = JEEContextFactory.getJEEContext();
            jeeContext.addResponseCookie(getCookie(""));
            return true;
        }
        return false;
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
