package com.baomidou.mipac4j.stateless.autoconfigure.aop;

import java.util.Collections;
import java.util.List;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.pac4j.core.authorization.authorizer.AbstractRequireElementAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAllPermissionsAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAllRolesAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyPermissionAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;

import com.baomidou.mipac4j.core.annotation.RequireAllPermission;
import com.baomidou.mipac4j.core.annotation.RequireAllRole;
import com.baomidou.mipac4j.core.annotation.RequireAnyPermission;
import com.baomidou.mipac4j.core.annotation.RequireAnyRole;
import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.context.session.NoSessionStore;
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

    private final SessionStore sessionStore = NoSessionStore.INSTANCE;
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
    private List<CommonProfile> isAuthenticated(J2EContext context) {
        final CommonProfile commonProfile = (CommonProfile) context.getRequestAttribute(Pac4jConstants.USER_PROFILES);
        List<CommonProfile> commonProfiles = Collections.singletonList(commonProfile);
        if (!IS_AUTHENTICATED_AUTHORIZER.isAuthorized(context, commonProfiles)) {
            throw HttpAction.unauthorized(context);
        }
        return commonProfiles;
    }

    private void isAuthorized(final AbstractRequireElementAuthorizer<String, CommonProfile> authorizer) {
        J2EContext j2EContext = J2EContextUtil.getJ2EContext(j2EContextFactory, sessionStore);
        final List<CommonProfile> profiles = this.isAuthenticated(j2EContext);
        if (!authorizer.isAuthorized(j2EContext, profiles)) {
            throw HttpAction.forbidden(j2EContext);
        }
    }
}
