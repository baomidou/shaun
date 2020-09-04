package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * @author miemie
 * @since 2020-09-04
 */
public class DefaultProfileManager implements ProfileManager {

    @Override
    public void afterLogin(TokenProfile profile) {
        // do nothing
    }

    @Override
    public boolean isAuthorized(TokenProfile profile) {
        // always true
        return true;
    }

    @Override
    public void afterLogout(TokenProfile profile) {
        // do nothing
    }
}
