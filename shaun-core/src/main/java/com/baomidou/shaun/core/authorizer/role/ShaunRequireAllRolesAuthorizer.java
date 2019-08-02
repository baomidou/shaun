package com.baomidou.shaun.core.authorizer.role;

import com.baomidou.shaun.core.authorizer.AbstranctAuthorizer;
import com.baomidou.shaun.core.authorizer.AuthorizationProfile;
import org.pac4j.core.profile.UserProfile;

/**
 * @author miemie
 * @since 2019-08-01
 */
public class ShaunRequireAllRolesAuthorizer<U extends UserProfile> extends AbstranctAuthorizer<U> {

    public ShaunRequireAllRolesAuthorizer(AuthorizationProfile<U> authorizationProfile, String... roles) {
        super(authorizationProfile, roles);
    }

    public static <U extends UserProfile> ShaunRequireAllRolesAuthorizer<U> requireAllRoles(AuthorizationProfile<U> authorizationProfile, String... roles) {
        return new ShaunRequireAllRolesAuthorizer<>(authorizationProfile, roles);
    }

    @Override
    public boolean isAuthorized(U profile) {
        return requireAll(authorizationProfile.roles(profile));
    }
}
