package com.baomidou.shaun.core.engine;

import com.baomidou.shaun.core.handler.callback.CallbackHandler;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;

/**
 * @author miemie
 * @since 2019-07-31
 */
public interface CallbackLogic {

    boolean perform(J2EContext context, Config config, String defaultUrl, CallbackHandler callbackExecutor);
}
