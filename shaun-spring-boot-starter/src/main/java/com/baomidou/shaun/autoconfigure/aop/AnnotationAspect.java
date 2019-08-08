package com.baomidou.shaun.autoconfigure.aop;

import java.util.Collections;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.profile.UserProfile;

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
        if (requireRoles.logical() == Logical.AND) {
            this.isAuthorized(ShaunRequireAllRolesAuthorizer.requireAllRoles(authorizationProfile, requireRoles.value()));
            return;
        }
        this.isAuthorized(ShaunRequireAnyRolesAuthorizer.requireAnyRole(authorizationProfile, requireRoles.value()));
    }

    @Before("@annotation(requirePermissions)")
    public void beforeRequireAllPermission(final RequirePermissions requirePermissions) {
        if (requirePermissions.logical() == Logical.AND) {
            this.isAuthorized(ShaunRequireAllPermissionsAuthorizer.requireAllPermissions(authorizationProfile, requirePermissions.value()));
            return;
        }
        this.isAuthorized(ShaunRequireAnyPermissionAuthorizer.requireAnyPermission(authorizationProfile, requirePermissions.value()));
    }

    private UserProfile isAuthenticated(JEEContext context) {
        final UserProfile userProfile = ProfileHolder.getProfile(context);
        if (!IS_AUTHENTICATED_AUTHORIZER.isAuthorized(context, Collections.singletonList(userProfile))) {
            throw UnauthorizedAction.INSTANCE;
        }
        return userProfile;
    }

    private void isAuthorized(final Authorizer<UserProfile> authorizer) {
        JEEContext j2EContext = JEEContextFactory.getJEEContext();
        final UserProfile profiles = this.isAuthenticated(j2EContext);
        if (!authorizer.isAuthorized(profiles)) {
            throw ForbiddenAction.INSTANCE;
        }
    }
}
