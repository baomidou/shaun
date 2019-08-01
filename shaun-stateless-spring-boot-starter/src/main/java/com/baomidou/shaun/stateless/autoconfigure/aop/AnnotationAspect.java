package com.baomidou.shaun.stateless.autoconfigure.aop;

import com.baomidou.shaun.core.annotation.RequireAllPermission;
import com.baomidou.shaun.core.annotation.RequireAllRole;
import com.baomidou.shaun.core.annotation.RequireAnyPermission;
import com.baomidou.shaun.core.annotation.RequireAnyRole;
import com.baomidou.shaun.core.util.JEEContextFactory;
import com.baomidou.shaun.core.util.ProfileHolder;
import com.baomidou.shaun.stateless.session.NoSessionStore;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.pac4j.core.authorization.authorizer.*;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.profile.CommonProfile;

import java.util.Collections;
import java.util.List;

/**
 * @author miemie
 * @since 2019-06-29
 */
@AllArgsConstructor
@Aspect
public class AnnotationAspect {

    private static final IsAuthenticatedAuthorizer IS_AUTHENTICATED_AUTHORIZER = new IsAuthenticatedAuthorizer();

    private final SessionStore<JEEContext> sessionStore = NoSessionStore.INSTANCE;

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
    private List<CommonProfile> isAuthenticated(JEEContext context) {
        final CommonProfile commonProfile = ProfileHolder.get(context, false);
        List<CommonProfile> commonProfiles = Collections.singletonList(commonProfile);
        if (!IS_AUTHENTICATED_AUTHORIZER.isAuthorized(context, commonProfiles)) {
            throw UnauthorizedAction.INSTANCE;
        }
        return commonProfiles;
    }

    private void isAuthorized(final AbstractRequireElementAuthorizer<String, CommonProfile> authorizer) {
        JEEContext j2EContext = JEEContextFactory.getJEEContext(sessionStore);
        final List<CommonProfile> profiles = this.isAuthenticated(j2EContext);
        if (!authorizer.isAuthorized(j2EContext, profiles)) {
            throw ForbiddenAction.INSTANCE;
        }
    }
}
