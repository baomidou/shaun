package com.baomidou.shaun.core.handler;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.UnauthorizedAction;

/**
 * 只能处理拦截到request的地方
 * 不能处理权限注解产生的异常
 *
 * @author miemie
 * @since 2019-08-08
 */
public interface HttpActionHandler {

    /**
     * 处理抛出的异常 {@link UnauthorizedAction} 和 {@link ForbiddenAction}
     *
     * @param action  异常
     * @param context 上下文
     */
    void preHandle(HttpAction action, JEEContext context);
}
