package com.baomidou.shaun.core.util;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.Set;

/**
 * 超管设置判断工具
 *
 * @author miemie
 * @since 2019-10-29
 */
public abstract class ShaunAdminProfileUtil {

    /**
     * 设置为管理员
     *
     * @param profile 用户
     * @param <U>     泛型
     */
    public static <U extends CommonProfile> void setAdmin(U profile) {
        profile.addRole("shaun-pac4j-admin-role");
        profile.addPermission("shaun-pac4j-admin-permission");
    }

    /**
     * 判断是否管理员
     *
     * @param profile 用户
     * @return 判断
     */
    public static boolean isAdmin(UserProfile profile) {
        Set<String> roles = profile.getRoles();
        if (CommonHelper.isEmpty(roles)) {
            return false;
        }
        if (!roles.contains("shaun-pac4j-admin-role")) {
            return false;
        }
        Set<String> permissions = profile.getPermissions();
        if (CommonHelper.isEmpty(permissions)) {
            return false;
        }
        return permissions.contains("shaun-pac4j-admin-permission");
    }
}
