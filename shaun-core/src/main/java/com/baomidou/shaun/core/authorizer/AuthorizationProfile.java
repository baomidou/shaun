package com.baomidou.shaun.core.authorizer;

import com.baomidou.shaun.core.enums.Logical;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.Set;

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
    default boolean checkRoles(Logical logical, Set<String> elements, Set<String> roles) {
        return match(logical, elements, roles);
    }

    /**
     * 校验 permission
     *
     * @return 是否通过
     */
    default boolean checkPermissions(Logical logical, Set<String> elements, Set<String> permissions) {
        return match(logical, elements, permissions);
    }

    /**
     * 验证
     *
     * @param elements    注解里的值
     * @param checkValues 根据用户取出来的值
     * @return 是否通过
     */
    default boolean match(Logical logical, Set<String> elements, Set<String> checkValues) {
        if (CommonHelper.isEmpty(elements) || CommonHelper.isEmpty(checkValues)) {
            return false;
        }
        if (logical == Logical.ANY) {
            for (String element : elements) {
                if (checkValues.contains(element)) {
                    return true;
                }
            }
            return false;
        }
        for (String element : elements) {
            if (!checkValues.contains(element)) {
                return false;
            }
        }
        return true;
    }
}
