package com.baomidou.shaun.autoconfigure.aop;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.profile.UserProfile;

import com.baomidou.shaun.core.annotation.HasAuthorization;
import com.baomidou.shaun.core.annotation.HasPermission;
import com.baomidou.shaun.core.annotation.HasRole;
import com.baomidou.shaun.core.authority.AuthorityManager;
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

    private final AuthorityManager authorityManager;

    @Before("@annotation(hasRole)")
    public void beforeHasRole(final HasRole hasRole) {
        if (!commonAuthorized(true, hasRole.logical(), toSet(hasRole.value()), authorityManager::roles)) {
            throw ForbiddenAction.INSTANCE;
        }
    }

    @Before("@annotation(hasPermission)")
    public void beforeHasPermission(final HasPermission hasPermission) {
        if (!commonAuthorized(false, hasPermission.logical(), toSet(hasPermission.value()), authorityManager::permissions)) {
            throw ForbiddenAction.INSTANCE;
        }
    }

    @Before("@annotation(hasAuthorization)")
    public void beforeHasAuthorization(final HasAuthorization hasAuthorization) {
        final Logical logical = hasAuthorization.logical();
        final HasRole role = hasAuthorization.roles();
        final Set<String> roles = toSet(role.value());
        final HasPermission permission = hasAuthorization.permissions();
        final Set<String> permissions = toSet(permission.value());
        JEEContext j2EContext = JEEContextFactory.getJEEContext();
        final UserProfile profiles = this.isAuthenticated(j2EContext);
        if (!authorityManager.isSkipAuthenticationUser(profiles)) {
            if (logical == Logical.ANY) {
                if (toCheck(profiles, true, role.logical(), roles, authorityManager::roles)
                        || toCheck(profiles, false, permission.logical(), permissions, authorityManager::permissions)) {
                    return;
                }
            } else {
                if (toCheck(profiles, true, role.logical(), roles, authorityManager::roles)
                        && toCheck(profiles, false, permission.logical(), permissions, authorityManager::permissions)) {
                    return;
                }
            }
            throw ForbiddenAction.INSTANCE;
        }
    }

    private UserProfile isAuthenticated(JEEContext context) {
        final UserProfile userProfile = ProfileHolder.getProfile(context);
        if (!IS_AUTHENTICATED_AUTHORIZER.isAuthorized(context, Collections.singletonList(userProfile))) {
            throw UnauthorizedAction.INSTANCE;
        }
        return userProfile;
    }

    private boolean commonAuthorized(final boolean isRole, final Logical logical,
                                     final Set<String> elements,
                                     final Function<UserProfile, Set<String>> checkValues) {
        JEEContext j2EContext = JEEContextFactory.getJEEContext();
        final UserProfile profiles = this.isAuthenticated(j2EContext);
        if (authorityManager.isSkipAuthenticationUser(profiles)) {
            return true;
        }
        return toCheck(profiles, isRole, logical, elements, checkValues);
    }

    private boolean toCheck(final UserProfile profiles, final boolean isRole, final Logical logical,
                            final Set<String> elements, final Function<UserProfile, Set<String>> checkValues) {
        if (isRole) {
            return authorityManager.checkRoles(logical, elements, checkValues.apply(profiles));
        }
        return authorityManager.checkPermissions(logical, elements, checkValues.apply(profiles));
    }

    private Set<String> toSet(String[] values) {
        return new HashSet<>(Arrays.asList(values));
    }
}
