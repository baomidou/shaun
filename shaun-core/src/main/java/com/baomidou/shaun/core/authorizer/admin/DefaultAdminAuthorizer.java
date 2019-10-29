package com.baomidou.shaun.core.authorizer.admin;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-10-29
 */
@AllArgsConstructor
public class DefaultAdminAuthorizer implements AdminAuthorizer {

    private final String adminRolePermission;

    @Override
    public <U extends CommonProfile> void setAdmin(U profile) {
        profile.addRole(adminRolePermission);
        profile.addPermission(adminRolePermission);
    }

    @Override
    public <U extends UserProfile> boolean isAdmin(U profile) {
        return profile.getRoles().contains(adminRolePermission) || profile.getPermissions().contains(adminRolePermission);
    }
}
