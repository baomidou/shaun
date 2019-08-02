package com.baomidou.shaun.core.authorizer.permission;

import com.baomidou.shaun.core.authorizer.AbstranctAuthorizer;
import com.baomidou.shaun.core.authorizer.AuthorizationProfile;
import org.pac4j.core.profile.UserProfile;

/**
 * @author miemie
 * @since 2019-08-01
 */
public class ShaunRequireAllPermissionsAuthorizer<U extends UserProfile> extends AbstranctAuthorizer<U> {

    public ShaunRequireAllPermissionsAuthorizer(AuthorizationProfile<U> authorizationProfile, String... permissions) {
        super(authorizationProfile, permissions);
    }

    public static <U extends UserProfile> ShaunRequireAllPermissionsAuthorizer<U> requireAllPermissions(AuthorizationProfile<U> authorizationProfile, String... permissions) {
        return new ShaunRequireAllPermissionsAuthorizer<>(authorizationProfile, permissions);
    }

    @Override
    public boolean isAuthorized(U profile) {
        return requireAll(authorizationProfile.permissions(profile));
    }
}
