package com.baomidou.shaun.core.util;

import org.pac4j.core.exception.http.BadRequestAction;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.UnauthorizedAction;

/**
 * @author miemie
 * @since 2024/5/30
 */
public class HttpActionInstance {
    /**
     * 400
     */
    public static final BadRequestAction BAD_REQUEST = new BadRequestAction();
    /**
     * 401
     */
    public static final UnauthorizedAction UNAUTHORIZED = new UnauthorizedAction();
    /**
     * 403
     */
    public static final ForbiddenAction FORBIDDEN = new ForbiddenAction();
}
