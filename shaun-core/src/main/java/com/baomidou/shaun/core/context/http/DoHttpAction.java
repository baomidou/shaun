package com.baomidou.shaun.core.context.http;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.exception.HttpAction;

/**
 * @author miemie
 * @since 2019-07-25
 */
public interface DoHttpAction {

    /**
     * 执行处理
     *
     * @param action  框架内产生的 HttpAction
     * @param context J2EContext
     */
    void adapt(HttpAction action, J2EContext context);
}
