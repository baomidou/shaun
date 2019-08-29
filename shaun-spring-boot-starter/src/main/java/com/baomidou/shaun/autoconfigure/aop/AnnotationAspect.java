package com.baomidou.shaun.autoconfigure.aop;

import com.baomidou.shaun.core.annotation.RequireAuthorization;
import com.baomidou.shaun.core.annotation.RequirePermissions;
import com.baomidou.shaun.core.annotation.RequireRoles;
import com.baomidou.shaun.core.authorizer.AuthorizationProfile;
import com.baomidou.shaun.core.authorizer.Authorizer;
import com.baomidou.shaun.core.authorizer.permission.ShaunRequireAllPermissionsAuthorizer;
import com.baomidou.shaun.core.authorizer.permission.ShaunRequireAnyPermissionAuthorizer;
import com.baomidou.shaun.core.authorizer.role.ShaunRequireAllRolesAuthorizer;
import com.baomidou.shaun.core.authorizer.role.ShaunRequireAnyRolesAuthorizer;
import com.baomidou.shaun.core.enums.Logical;
import com.baomidou.shaun.core.util.JEEContextFactory;
import com.baomidou.shaun.core.util.ProfileHolder;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.profile.UserProfile;

import java.util.Collections;

/**
 * @author miemie
 * @since 2019-06-29
 */
@AllArgsConstructor
@Aspect
public class AnnotationAspect {

    private static final IsAuthenticatedAuthorizer<UserProfile> IS_AUTHENTICATED_AUTHORIZER = new IsAuthenticatedAuthorizer<>();

    private final AuthorizationProfile<UserProfile> authorizationProfile;

    @Before("@annotation(requireRoles)")
    public void beforeRequireAnyRole(final RequireRoles requireRoles) {
        boolean a;
        if (requireRoles.logical() == Logical.AND) {
            a = this.isAuthorized(ShaunRequireAllRolesAuthorizer.requireAllRoles(authorizationProfile, requireRoles.value()));
        } else {
            a = this.isAuthorized(ShaunRequireAnyRolesAuthorizer.requireAnyRole(authorizationProfile, requireRoles.value()));
        }
        if (!a) {
            throw ForbiddenAction.INSTANCE;
        }
    }

    @Before("@annotation(requirePermissions)")
    public void beforeRequireAllPermission(final RequirePermissions requirePermissions) {
        boolean a;
        if (requirePermissions.logical() == Logical.AND) {
            a = this.isAuthorized(ShaunRequireAllPermissionsAuthorizer.requireAllPermissions(authorizationProfile, requirePermissions.value()));
        } else {
            a = this.isAuthorized(ShaunRequireAnyPermissionAuthorizer.requireAnyPermission(authorizationProfile, requirePermissions.value()));
        }
        if (!a) {
            throw ForbiddenAction.INSTANCE;
        }
    }

    @Before("@annotation(requireAuthorization)")
    public void beforeRequireAllPermission(final RequireAuthorization requireAuthorization) {
        final Logical logical = requireAuthorization.logical();
        final RequireRoles roles = requireAuthorization.roles();
        final RequirePermissions permissions = requireAuthorization.permissions();
        if (logical == Logical.AND) {
            boolean a;
            if (roles.logical() == Logical.AND) {
                a = this.isAuthorized(ShaunRequireAllRolesAuthorizer.requireAllRoles(authorizationProfile, roles.value()));
            } else {
                a = this.isAuthorized(ShaunRequireAnyRolesAuthorizer.requireAnyRole(authorizationProfile, roles.value()));
            }
            if (!a) {
                throw ForbiddenAction.INSTANCE;
            }
            if (permissions.logical() == Logical.AND) {
                a = this.isAuthorized(ShaunRequireAllPermissionsAuthorizer.requireAllPermissions(authorizationProfile, permissions.value()));
            } else {
                a = this.isAuthorized(ShaunRequireAnyPermissionAuthorizer.requireAnyPermission(authorizationProfile, permissions.value()));
            }
            if (!a) {
                throw ForbiddenAction.INSTANCE;
            }
        } else {
            boolean a;
            if (roles.logical() == Logical.AND) {
                a = this.isAuthorized(ShaunRequireAllRolesAuthorizer.requireAllRoles(authorizationProfile, roles.value()));
            } else {
                a = this.isAuthorized(ShaunRequireAnyRolesAuthorizer.requireAnyRole(authorizationProfile, roles.value()));
            }
            if (a) {
                return;
            }
            boolean b;
            if (permissions.logical() == Logical.AND) {
                b = this.isAuthorized(ShaunRequireAllPermissionsAuthorizer.requireAllPermissions(authorizationProfile, permissions.value()));
            } else {
                b = this.isAuthorized(ShaunRequireAnyPermissionAuthorizer.requireAnyPermission(authorizationProfile, permissions.value()));
            }
            if (!b) {
                throw ForbiddenAction.INSTANCE;
            }
        }
    }

    private UserProfile isAuthenticated(JEEContext context) {
        final UserProfile userProfile = ProfileHolder.getProfile(context);
        if (!IS_AUTHENTICATED_AUTHORIZER.isAuthorized(context, Collections.singletonList(userProfile))) {
            throw UnauthorizedAction.INSTANCE;
        }
        return userProfile;
    }

    private boolean isAuthorized(final Authorizer<UserProfile> authorizer) {
        JEEContext j2EContext = JEEContextFactory.getJEEContext();
        final UserProfile profiles = this.isAuthenticated(j2EContext);
        return authorizer.isAuthorized(profiles);
    }
}
