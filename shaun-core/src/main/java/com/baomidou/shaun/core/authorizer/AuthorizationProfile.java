package com.baomidou.shaun.core.authorizer;

import java.util.Set;

import org.pac4j.core.profile.UserProfile;

import com.baomidou.shaun.core.enums.Logical;

/**
 * 用户 role 和 permission 获取类
 *
 * @author miemie
 * @since 2019-08-01
 */
public interface AuthorizationProfile {

    /**
     * 获取这个用户有的 roles
     *
     * @param profile 用户
     * @return roles
     */
    default <U extends UserProfile> Set<String> roles(U profile) {
        return profile.getRoles();
    }

    /**
     * 获取这个用户有的 permissions
     *
     * @param profile 用户
     * @return permissions
     */
    default <U extends UserProfile> Set<String> permissions(U profile) {
        return profile.getPermissions();
    }

    /**
     * 校验 role
     *
     * @return 是否通过
     */
    boolean checkRoles(Logical logical, Set<String> elements, Set<String> roles);

    /**
     * 校验 permission
     *
     * @return 是否通过
     */
    boolean checkPermissions(Logical logical, Set<String> elements, Set<String> permissions);
}
