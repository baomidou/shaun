package com.baomidou.shaun.core.authorizer.permission;

import java.util.Set;

import org.pac4j.core.authorization.authorizer.RequireAllPermissionsAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import com.baomidou.shaun.core.authorizer.AuthorizationContext;

/**
 * @author miemie
 * @since 2019-08-01
 */
public class ShaunRequireAllPermissionsAuthorizer<U extends UserProfile> extends RequireAllPermissionsAuthorizer<U> {

    private final AuthorizationContext<U> authorizationContext;

    public ShaunRequireAllPermissionsAuthorizer(AuthorizationContext<U> authorizationContext, String... permissions) {
        super(permissions);
        this.authorizationContext = authorizationContext;
    }

    public static <U extends UserProfile> RequireAllPermissionsAuthorizer<U> requireAllPermissions(AuthorizationContext<U> authorizationContext, String... permissions) {
        return new ShaunRequireAllPermissionsAuthorizer<>(authorizationContext, permissions);
    }

    @Override
    protected boolean check(WebContext context, U profile, String element) {
        Set<String> permissions = authorizationContext.permissions(profile);
        return permissions.contains(element);
    }
}
