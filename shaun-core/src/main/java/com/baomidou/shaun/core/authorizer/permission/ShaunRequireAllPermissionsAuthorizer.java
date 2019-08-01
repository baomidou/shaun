package com.baomidou.shaun.core.authorizer.permission;

import java.util.Set;

import org.pac4j.core.authorization.authorizer.RequireAllPermissionsAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import com.baomidou.shaun.core.authorizer.AuthorizationProfile;

/**
 * @author miemie
 * @since 2019-08-01
 */
public class ShaunRequireAllPermissionsAuthorizer<U extends UserProfile> extends RequireAllPermissionsAuthorizer<U> {

    private final AuthorizationProfile<U> authorizationProfile;

    public ShaunRequireAllPermissionsAuthorizer(AuthorizationProfile<U> authorizationProfile, String... permissions) {
        super(permissions);
        this.authorizationProfile = authorizationProfile;
    }

    public static <U extends UserProfile> RequireAllPermissionsAuthorizer<U> requireAllPermissions(AuthorizationProfile<U> authorizationProfile, String... permissions) {
        return new ShaunRequireAllPermissionsAuthorizer<>(authorizationProfile, permissions);
    }

    @Override
    protected boolean check(WebContext context, U profile, String element) {
        Set<String> permissions = authorizationProfile.permissions(profile);
        return permissions.contains(element);
    }
}
