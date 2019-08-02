package com.baomidou.shaun.core.authorizer.role;

import com.baomidou.shaun.core.authorizer.AbstranctAuthorizer;
import com.baomidou.shaun.core.authorizer.AuthorizationProfile;
import org.pac4j.core.profile.UserProfile;

/**
 * @author miemie
 * @since 2019-08-01
 */
public class ShaunRequireAnyRolesAuthorizer<U extends UserProfile> extends AbstranctAuthorizer<U> {

    public ShaunRequireAnyRolesAuthorizer(AuthorizationProfile<U> authorizationProfile, String... roles) {
        super(authorizationProfile, roles);
    }

    public static <U extends UserProfile> ShaunRequireAnyRolesAuthorizer<U> requireAnyRole(AuthorizationProfile<U> authorizationProfile, String... roles) {
        return new ShaunRequireAnyRolesAuthorizer<>(authorizationProfile, roles);
    }

    @Override
    public boolean isAuthorized(U profile) {
        return requireAny(profile.getRoles());
    }
}
