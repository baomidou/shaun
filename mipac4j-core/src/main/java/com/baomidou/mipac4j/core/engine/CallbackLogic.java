package com.baomidou.mipac4j.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;

import com.baomidou.mipac4j.core.adapter.CommonProfileAdapter;

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
     * @param indexUrl          the index url
     * @return the resulting action of the callback
     */
    R perform(C context, Config config, final HttpActionAdapter<R, C> httpActionAdapter,
              String indexUrl, CommonProfileAdapter<P, CommonProfile> commonProfileAdapter);
}
