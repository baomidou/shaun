package com.baomidou.shaun.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;

/**
 * @author miemie
 * @since 2019-07-31
 */
public interface SecurityLogic {

    boolean perform(J2EContext context, Config config, String clients, String authorizers, String matchers);
}
