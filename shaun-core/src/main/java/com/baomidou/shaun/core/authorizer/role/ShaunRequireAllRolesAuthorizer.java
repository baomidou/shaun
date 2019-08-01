package com.baomidou.shaun.core.authorizer.role;

import java.util.Set;

import org.pac4j.core.authorization.authorizer.RequireAllRolesAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import com.baomidou.shaun.core.authorizer.AuthorizationContext;

/**
 * @author miemie
 * @since 2019-08-01
 */
public class ShaunRequireAllRolesAuthorizer<U extends UserProfile> extends RequireAllRolesAuthorizer<U> {

    private final AuthorizationContext<U> authorizationContext;

    public ShaunRequireAllRolesAuthorizer(AuthorizationContext<U> authorizationContext, String... roles) {
        super(roles);
        this.authorizationContext = authorizationContext;
    }

    public static <U extends UserProfile> RequireAllRolesAuthorizer<U> requireAllRoles(AuthorizationContext<U> authorizationContext, String... roles) {
        return new ShaunRequireAllRolesAuthorizer<>(authorizationContext, roles);
    }

    @Override
    protected boolean check(WebContext context, U profile, String element) {
        Set<String> roles = authorizationContext.roles(profile);
        return roles.contains(element);
    }
}
