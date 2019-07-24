package com.baomidou.mipac4j.core.engine;

import com.baomidou.mipac4j.core.adapter.HttpActionAdapter;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * copy from {@link org.pac4j.core.engine.CallbackLogic}
 * <p>
 * 做了一些定制化改造
 *
 * @author miemie
 * @since 2019-07-24
 */
public interface CallbackLogic<R, C extends WebContext, P extends CommonProfile> {

    /**
     * Perform the callback logic.
     *
     * @param context           the web context
     * @param config            the security configuration
     * @param httpActionAdapter the HTTP action adapter
     * @param defaultUrl        the default url
     * @param saveInSession     whether profile should be saved in session
     * @param multiProfile      whether multi profiles are supported
     * @param renewSession      whether the session must be renewed
     * @param client            the default client
     * @return the resulting action of the callback
     */
    R perform(C context, Config config, HttpActionAdapter<R, P> httpActionAdapter,
              String defaultUrl, Boolean saveInSession, Boolean multiProfile, Boolean renewSession, String client);
}
