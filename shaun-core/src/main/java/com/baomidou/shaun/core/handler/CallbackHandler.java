package com.baomidou.shaun.core.handler;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-26
 */
public interface CallbackHandler<R extends CommonProfile, U extends CommonProfile> {

    R callBack(WebContext context, U profile);
}
