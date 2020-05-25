package com.baomidou.shaun.core.context;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * @author miemie
 * @since 2020-05-25
 */
public interface ProfileHolderStrategy {

    /**
     * Clears the current TokenProfile.
     */
    void clearProfile();

    /**
     * Obtains the current TokenProfile.
     */
    TokenProfile getProfile();

    /**
     * Sets the current TokenProfile.
     */
    void setProfile(TokenProfile profile);
}
