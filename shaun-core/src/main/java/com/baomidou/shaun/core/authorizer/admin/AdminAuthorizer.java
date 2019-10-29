package com.baomidou.shaun.core.authorizer.admin;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

/**
 * @author miemie
 * @since 2019-10-29
 */
public interface AdminAuthorizer {

    <U extends CommonProfile> void setAdmin(U profile);

    /**
     * 判断是否是管理员
     *
     * @param profile 用户
     * @return 是否管理员
     */
    <U extends UserProfile> boolean isAdmin(U profile);
}
