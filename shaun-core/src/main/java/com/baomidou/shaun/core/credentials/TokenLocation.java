package com.baomidou.shaun.core.credentials;

/**
 * token 存放位置
 *
 * @author miemie
 * @since 2019-07-20
 */
public enum TokenLocation {
    /**
     * 请求头
     */
    HEADER,
    /**
     * cookie
     */
    COOKIE,
    /**
     * 请求的 parameter
     */
    PARAMETER,
    /**
     * 请求头 和 cookie
     */
    HEADER_OR_COOKIE,
    /**
     * 请求头 和 请求的 parameter
     */
    HEADER_OR_PARAMETER,
    /**
     * 请求头 和 cookie 和 请求的 parameter
     */
    HEADER_OR_COOKIE_OR_PARAMETER;

    public boolean enableCookie() {
        return this == COOKIE || this == HEADER_OR_COOKIE || this == HEADER_OR_COOKIE_OR_PARAMETER;
    }
}
