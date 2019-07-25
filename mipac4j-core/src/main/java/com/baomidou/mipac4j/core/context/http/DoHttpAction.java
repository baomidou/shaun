package com.baomidou.mipac4j.core.context.http;

import org.pac4j.core.context.J2EContext;

/**
 * @author miemie
 * @since 2019-07-25
 */
public interface DoHttpAction {

    /**
     * 执行处理
     *
     * @param code    框架内产生的 http code
     * @param context J2EContext
     */
    void adapt(int code, J2EContext context);
}
