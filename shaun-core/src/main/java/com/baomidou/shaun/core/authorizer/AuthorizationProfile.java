package com.baomidou.shaun.core.authorizer;

import java.util.Set;

import org.pac4j.core.profile.UserProfile;

/**
 * 用户 role 和 permission 获取类
 *
 * @author miemie
 * @since 2019-08-01
 */
public interface AuthorizationProfile<U extends UserProfile> {

    /**
     * 获取这个用户有的 roles
     *
     * @param profile 用户
     * @return roles
     */
    default Set<String> roles(U profile) {
        return profile.getRoles();
    }

    /**
     * 获取这个用户有的 permissions
     *
     * @param profile 用户
     * @return permissions
     */
    default Set<String> permissions(U profile) {
        return profile.getPermissions();
    }
}
