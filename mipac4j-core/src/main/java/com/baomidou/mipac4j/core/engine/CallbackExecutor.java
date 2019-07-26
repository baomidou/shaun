package com.baomidou.mipac4j.core.engine;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.Optional;

/**
 * @author miemie
 * @since 2019-07-26
 */
public interface CallbackExecutor<U extends CommonProfile> {

    void logout(WebContext context, Optional<U> profile);
}
