package com.baomidou.shaun.core.engine;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-26
 */
public interface CallbackExecutor<U extends CommonProfile> {

    CallbackExecutor<CommonProfile> DO_NOTHING = (ctx, pf) -> {
    };

    void callBack(WebContext context, U profile);
}
