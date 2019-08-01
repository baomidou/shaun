package com.baomidou.shaun.core.authorizer.role;

import java.util.Set;

import org.pac4j.core.authorization.authorizer.RequireAllRolesAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import com.baomidou.shaun.core.authorizer.AuthorizationProfile;

/**
 * @author miemie
 * @since 2019-08-01
 */
public class ShaunRequireAllRolesAuthorizer<U extends UserProfile> extends RequireAllRolesAuthorizer<U> {

    private final AuthorizationProfile<U> authorizationProfile;

    public ShaunRequireAllRolesAuthorizer(AuthorizationProfile<U> authorizationProfile, String... roles) {
        super(roles);
        this.authorizationProfile = authorizationProfile;
    }

    public static <U extends UserProfile> RequireAllRolesAuthorizer<U> requireAllRoles(AuthorizationProfile<U> authorizationProfile, String... roles) {
        return new ShaunRequireAllRolesAuthorizer<>(authorizationProfile, roles);
    }

    @Override
    protected boolean check(WebContext context, U profile, String element) {
        Set<String> roles = authorizationProfile.roles(profile);
        return roles.contains(element);
    }
}
