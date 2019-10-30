package com.baomidou.shaun.core.authorizer;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-08-01
 */
@AllArgsConstructor
public class DefaultAuthorizationInterceptor implements AuthorizationInterceptor {

    private final String skipAuthenticationRolePermission;

    @Override
    public <U extends CommonProfile> void setUserSkipAuthentication(U profile) {
        profile.addRole(skipAuthenticationRolePermission);
        profile.addPermission(skipAuthenticationRolePermission);
    }

    @Override
    public <U extends UserProfile> boolean isSkipAuthenticationUser(U profile) {
        return profile.getRoles().contains(skipAuthenticationRolePermission) ||
                profile.getPermissions().contains(skipAuthenticationRolePermission);
    }
}
