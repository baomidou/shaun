package com.baomidou.shaun.stateless.autoconfigure.aop;

import java.util.Collections;
import java.util.List;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.pac4j.core.authorization.authorizer.AbstractRequireElementAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.profile.UserProfile;

import com.baomidou.shaun.core.annotation.RequireAllPermission;
import com.baomidou.shaun.core.annotation.RequireAllRole;
import com.baomidou.shaun.core.annotation.RequireAnyPermission;
import com.baomidou.shaun.core.annotation.RequireAnyRole;
import com.baomidou.shaun.core.authorizer.AuthorizationProfile;
import com.baomidou.shaun.core.authorizer.permission.ShaunRequireAllPermissionsAuthorizer;
import com.baomidou.shaun.core.authorizer.permission.ShaunRequireAnyPermissionAuthorizer;
import com.baomidou.shaun.core.authorizer.role.ShaunRequireAllRolesAuthorizer;
import com.baomidou.shaun.core.authorizer.role.ShaunRequireAnyRolesAuthorizer;
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

    @Before("@annotation(requireAnyRole)")
    public void beforeRequireAnyRole(final RequireAnyRole requireAnyRole) {
        this.isAuthorized(ShaunRequireAnyRolesAuthorizer.requireAnyRole(authorizationProfile, requireAnyRole.value()));
    }

    @Before("@annotation(requireAllRole)")
    public void beforeRequireAllRole(final RequireAllRole requireAllRole) {
        this.isAuthorized(ShaunRequireAllRolesAuthorizer.requireAllRoles(authorizationProfile, requireAllRole.value()));
    }

    @Before("@annotation(requireAnyPermission)")
    public void beforeRequireAnyPermission(final RequireAnyPermission requireAnyPermission) {
        this.isAuthorized(ShaunRequireAnyPermissionAuthorizer.requireAnyPermission(authorizationProfile, requireAnyPermission.value()));
    }

    @Before("@annotation(requireAllPermission)")
    public void beforeRequireAllPermission(final RequireAllPermission requireAllPermission) {
        this.isAuthorized(ShaunRequireAllPermissionsAuthorizer.requireAllPermissions(authorizationProfile, requireAllPermission.value()));
    }

    private List<UserProfile> isAuthenticated(JEEContext context) {
        final UserProfile userProfile = ProfileHolder.get(context, false);
        List<UserProfile> userProfiles = Collections.singletonList(userProfile);
        if (!IS_AUTHENTICATED_AUTHORIZER.isAuthorized(context, userProfiles)) {
            throw UnauthorizedAction.INSTANCE;
        }
        return userProfiles;
    }

    private void isAuthorized(final AbstractRequireElementAuthorizer<String, UserProfile> authorizer) {
        JEEContext j2EContext = JEEContextFactory.getJEEContext();
        final List<UserProfile> profiles = this.isAuthenticated(j2EContext);
        if (!authorizer.isAuthorized(j2EContext, profiles)) {
            throw ForbiddenAction.INSTANCE;
        }
    }
}
