package com.baomidou.mipac4j.core.adapter;

import org.pac4j.core.client.Client;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-24
 */
@FunctionalInterface
public interface CommonProfileAdapter {

    /**
     * Adapt the HTTP action.
     *
     * @param profile the user profile
     * @return the specific framework HTTP result
     */
    CommonProfile adapt(CommonProfile profile, Client client);
}
