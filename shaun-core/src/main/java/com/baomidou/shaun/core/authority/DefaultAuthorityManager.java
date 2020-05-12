package com.baomidou.shaun.core.authority;

import com.baomidou.shaun.core.profile.TokenProfile;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-08-01
 */
@AllArgsConstructor
public class DefaultAuthorityManager implements AuthorityManager {

    private final String skipAuthenticationRolePermission;

    @Override
    public void setUserSkipAuthentication(TokenProfile profile) {
        profile.addRole(skipAuthenticationRolePermission);
        profile.addPermission(skipAuthenticationRolePermission);
    }

    @Override
    public boolean isSkipAuthenticationUser(TokenProfile profile) {
        return profile.getRoles().contains(skipAuthenticationRolePermission) ||
                profile.getPermissions().contains(skipAuthenticationRolePermission);
    }
}
