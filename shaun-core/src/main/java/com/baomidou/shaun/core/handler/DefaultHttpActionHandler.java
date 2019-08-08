package com.baomidou.shaun.core.handler;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;

/**
 * 默认不做处理,继续上抛
 *
 * @author miemie
 * @since 2019-08-08
 */
public class DefaultHttpActionHandler implements HttpActionHandler {

    @Override
    public void preHandle(HttpAction action, JEEContext context) {
        throw action;
    }
}
