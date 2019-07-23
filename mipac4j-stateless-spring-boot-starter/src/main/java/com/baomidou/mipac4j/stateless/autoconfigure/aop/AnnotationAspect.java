package com.baomidou.mipac4j.stateless.autoconfigure.aop;

import java.util.List;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.pac4j.core.authorization.authorizer.AbstractRequireElementAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAllPermissionsAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAllRolesAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyPermissionAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.jwt.profile.JwtProfile;

import com.baomidou.mipac4j.core.annotation.RequireAllPermission;
import com.baomidou.mipac4j.core.annotation.RequireAllRole;
import com.baomidou.mipac4j.core.annotation.RequireAnyPermission;
import com.baomidou.mipac4j.core.annotation.RequireAnyRole;
import com.baomidou.mipac4j.core.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.util.J2EContextUtil;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-06-29
 */
@AllArgsConstructor
@Aspect
public class AnnotationAspect {

    private static final IsAuthenticatedAuthorizer IS_AUTHENTICATED_AUTHORIZER = new IsAuthenticatedAuthorizer();

    private final Config config;
    private final J2EContextFactory j2EContextFactory;

    @Before("@annotation(requireAnyRole)")
    public void beforeRequireAnyRole(final RequireAnyRole requireAnyRole) {
        this.isAuthorized(RequireAnyRoleAuthorizer.requireAnyRole(requireAnyRole.value()));
    }

    @Before("@annotation(requireAllRole)")
    public void beforeRequireAllRole(final RequireAllRole requireAllRole) {
        this.isAuthorized(RequireAllRolesAuthorizer.requireAllRoles(requireAllRole.value()));
    }

    @Before("@annotation(requireAnyPermission)")
    public void beforeRequireAnyPermission(final RequireAnyPermission requireAnyPermission) {
        this.isAuthorized(RequireAnyPermissionAuthorizer.requireAnyPermission(requireAnyPermission.value()));
    }

    @Before("@annotation(requireAllPermission)")
    public void beforeRequireAllPermission(final RequireAllPermission requireAllPermission) {
        this.isAuthorized(RequireAllPermissionsAuthorizer.requireAllPermissions(requireAllPermission.value()));
    }

    @SuppressWarnings("unchecked")
    private List<JwtProfile> isAuthenticated(J2EContext webContext) {
        final List<JwtProfile> profiles = config.getProfileManagerFactory().apply(webContext).getAll(false);
        if (!IS_AUTHENTICATED_AUTHORIZER.isAuthorized(webContext, profiles)) {
            throw HttpAction.unauthorized(webContext);
        }
        return profiles;
    }

    private void isAuthorized(final AbstractRequireElementAuthorizer<String, JwtProfile> authorizer) {
        J2EContext j2EContext = J2EContextUtil.getJ2EContext(j2EContextFactory, config.getSessionStore());
        final List<JwtProfile> profiles = this.isAuthenticated(j2EContext);
        if (!authorizer.isAuthorized(j2EContext, profiles)) {
            throw HttpAction.forbidden(j2EContext);
        }
    }
}
