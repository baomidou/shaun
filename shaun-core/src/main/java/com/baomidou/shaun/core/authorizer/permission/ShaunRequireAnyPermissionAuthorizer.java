package com.baomidou.shaun.core.authorizer.permission;

import com.baomidou.shaun.core.authorizer.AbstranctAuthorizer;
import com.baomidou.shaun.core.authorizer.AuthorizationProfile;
import com.baomidou.shaun.core.util.ShaunAdminProfileUtil;
import org.pac4j.core.profile.UserProfile;

/**
 * @author miemie
 * @since 2019-08-01
 */
public class ShaunRequireAnyPermissionAuthorizer<U extends UserProfile> extends AbstranctAuthorizer<U> {

    public ShaunRequireAnyPermissionAuthorizer(AuthorizationProfile<U> authorizationProfile, String... permissions) {
        super(authorizationProfile, permissions);
    }

    public static <U extends UserProfile> ShaunRequireAnyPermissionAuthorizer<U> requireAnyPermission(AuthorizationProfile<U> authorizationProfile, String... permissions) {
        return new ShaunRequireAnyPermissionAuthorizer<>(authorizationProfile, permissions);
    }

    @Override
    public boolean isAuthorized(U profile) {
        if (ShaunAdminProfileUtil.isAdmin(profile)) {
            return true;
        }
        return requireAny(authorizationProfile.permissions(profile));
    }
}
