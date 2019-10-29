package com.baomidou.shaun.core.authorizer.admin;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

/**
 * @author miemie
 * @since 2019-10-29
 */
public interface AdminAuthorizer {

    /**
     * 设置用户为管理员
     *
     * @param profile 用户
     * @param <U>     泛型
     */
    <U extends CommonProfile> void setAdmin(U profile);

    /**
     * 判断是否是管理员
     *
     * @param profile 用户
     * @return 是否管理员
     */
    <U extends UserProfile> boolean isAdmin(U profile);
}
