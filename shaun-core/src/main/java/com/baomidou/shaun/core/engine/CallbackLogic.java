package com.baomidou.shaun.core.engine;

import com.baomidou.shaun.core.handler.CallbackHandler;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;

/**
 * @author miemie
 * @since 2019-07-31
 */
public interface CallbackLogic {

    boolean perform(JEEContext context, Config config, String defaultUrl, CallbackHandler callbackExecutor);
}
