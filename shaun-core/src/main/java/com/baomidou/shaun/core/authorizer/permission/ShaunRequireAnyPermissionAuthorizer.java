package com.baomidou.shaun.core.authorizer.permission;

import java.util.Set;

import org.pac4j.core.authorization.authorizer.RequireAnyPermissionAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import com.baomidou.shaun.core.authorizer.AuthorizationContext;

/**
 * @author miemie
 * @since 2019-08-01
 */
public class ShaunRequireAnyPermissionAuthorizer<U extends UserProfile> extends RequireAnyPermissionAuthorizer<U> {

    private final AuthorizationContext<U> authorizationContext;

    public ShaunRequireAnyPermissionAuthorizer(AuthorizationContext<U> authorizationContext, String... permissions) {
        super(permissions);
        this.authorizationContext = authorizationContext;
    }

    public static <U extends UserProfile> RequireAnyPermissionAuthorizer<U> requireAnyPermission(AuthorizationContext<U> authorizationContext, String... permissions) {
        return new ShaunRequireAnyPermissionAuthorizer<>(authorizationContext, permissions);
    }

    @Override
    protected boolean check(WebContext context, U profile, String element) {
        Set<String> permissions = authorizationContext.permissions(profile);
        return permissions.contains(element);
    }
}
