package com.baomidou.shaun.core.authorizer.role;

import java.util.Set;

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import com.baomidou.shaun.core.authorizer.AuthorizationProfile;

/**
 * @author miemie
 * @since 2019-08-01
 */
public class ShaunRequireAnyRolesAuthorizer<U extends UserProfile> extends RequireAnyRoleAuthorizer<U> {

    private final AuthorizationProfile<U> authorizationProfile;

    public ShaunRequireAnyRolesAuthorizer(AuthorizationProfile<U> authorizationProfile, String... roles) {
        super(roles);
        this.authorizationProfile = authorizationProfile;
    }

    public static <U extends UserProfile> RequireAnyRoleAuthorizer<U> requireAnyRole(AuthorizationProfile<U> authorizationProfile, String... roles) {
        return new ShaunRequireAnyRolesAuthorizer<>(authorizationProfile, roles);
    }

    @Override
    protected boolean check(WebContext context, U profile, String element) {
        Set<String> roles = authorizationProfile.roles(profile);
        return roles.contains(element);
    }
}
