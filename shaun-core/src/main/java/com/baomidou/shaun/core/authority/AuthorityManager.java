package com.baomidou.shaun.core.authority;

import com.baomidou.shaun.core.annotation.Logical;
import com.baomidou.shaun.core.profile.TokenProfile;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * 用户 role 和 permission 授权,鉴权 类
 *
 * @author miemie
 * @since 2019-08-01
 */
public interface AuthorityManager {

    /**
     * 设置用户为跳过所有鉴权
     *
     * @param profile 用户
     */
    void skipAuthentication(TokenProfile profile);

    /**
     * 判断是否要跳过所有鉴权
     *
     * @param profile 用户
     * @return 是否
     */
    boolean isSkipAuthentication(TokenProfile profile);

    /**
     * 获取这个用户有的 roles
     *
     * @param profile 用户
     * @return roles
     */
    default Set<String> roles(TokenProfile profile) {
        return profile.getRoles();
    }

    /**
     * 获取这个用户有的 permissions
     *
     * @param profile 用户
     * @return permissions
     */
    default Set<String> permissions(TokenProfile profile) {
        return profile.getPermissions();
    }

    /**
     * 校验 role
     *
     * @param logical  模式
     * @param elements 需要具有的角色
     * @param roles    用户实际拥有的角色
     * @return 是否通过
     */
    default boolean checkRoles(Logical logical, Set<String> elements, Set<String> roles) {
        return match(logical, elements, roles);
    }

    /**
     * 校验 permission
     *
     * @param logical     模式
     * @param elements    需要具有的权限
     * @param permissions 用户实际拥有的权限
     * @return 是否通过
     */
    default boolean checkPermissions(Logical logical, Set<String> elements, Set<String> permissions) {
        return match(logical, elements, permissions);
    }

    /**
     * 验证
     *
     * @param elements    需要具有的
     * @param checkValues 实际拥有的
     * @return 是否通过
     */
    default boolean match(Logical logical, Set<String> elements, Set<String> checkValues) {
        if (CollectionUtils.isEmpty(elements) || CollectionUtils.isEmpty(checkValues)) {
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
