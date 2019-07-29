package com.baomidou.mipac4j.core.context.http;

import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.exception.HttpAction;

/**
 * @author miemie
 * @since 2019-07-25
 */
@Slf4j
public class DefaultDoHttpAction implements DoHttpAction {

    @Override
    public void adapt(HttpAction action, J2EContext context) {
        log.debug("code is {}", action.getCode());
    }
}
