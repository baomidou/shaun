package com.baomidou.shaun.core.properties;

import org.pac4j.core.context.Pac4jConstants;

import lombok.Data;

/**
 * @author miemie
 * @since 2019-07-20
 */
@Data
public class Cookie {

    private String name = Pac4jConstants.SESSION_ID;
    private int version = 0;
    private String comment;
    private String domain = "";
    private String path = Pac4jConstants.DEFAULT_URL_VALUE;
    private boolean secure;
    private boolean isHttpOnly;

    /**
     * 获取pac4j的cookie
     *
     * @param token  token
     * @param maxAge 存活时间
     * @return cookie
     */
    public org.pac4j.core.context.Cookie getPac4jCookie(final String token, int maxAge) {
        org.pac4j.core.context.Cookie cookie = new org.pac4j.core.context.Cookie(name, token);
        cookie.setVersion(version);
        cookie.setSecure(secure);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setComment(comment);
        cookie.setDomain(domain);
        return cookie;
    }
}
