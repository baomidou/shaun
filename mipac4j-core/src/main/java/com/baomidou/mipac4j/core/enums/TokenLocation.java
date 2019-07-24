package com.baomidou.mipac4j.core.enums;

/**
 * 没有 session,不推荐使用 session 存任何东西
 * 本项目除个别地方会用到 session 外(流程正常的话会及时销毁 session)
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
    HEADER_OR_COOKIE_OR_PARAMETER
}
