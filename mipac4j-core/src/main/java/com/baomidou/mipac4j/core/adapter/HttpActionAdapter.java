package com.baomidou.mipac4j.core.adapter;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-24
 */
public interface HttpActionAdapter<R extends CommonProfile, P extends CommonProfile> {

    /**
     * Adapt the HTTP action.
     *
     * @param profile the user profile
     * @return the specific framework HTTP result
     */
    R adapt(P profile, IndirectClient client);
}
