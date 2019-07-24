package com.baomidou.mipac4j.core.adapter;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-24
 */
@FunctionalInterface
public interface CommonProfileAdapter<T extends CommonProfile, R extends CommonProfile> {

    /**
     * Adapt the HTTP action.
     *
     * @param profile the user profile
     * @return the specific framework HTTP result
     */
    R adapt(T profile, IndirectClient client);
}
