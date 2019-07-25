package com.baomidou.mipac4j.core.context.http;

import org.pac4j.core.context.J2EContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author miemie
 * @since 2019-07-25
 */
@Slf4j
public class DefaultDoHttpAction implements DoHttpAction {

    @Override
    public void adapt(int code, J2EContext context) {
        log.debug("code is {}", code);
    }
}
