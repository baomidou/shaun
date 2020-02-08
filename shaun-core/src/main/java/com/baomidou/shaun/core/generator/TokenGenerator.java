package com.baomidou.shaun.core.generator;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * token 生成器
 *
 * @author miemie
 * @since 2019-07-20
 */
public interface TokenGenerator {

    /**
     * @param profile                  profile
     * @param isSkipAuthenticationUser 是否是跳过所有鉴权的用户
     * @return token
     */
    String generate(final TokenProfile profile, final boolean isSkipAuthenticationUser, String expireTime);

    /**
     * 获取存活时间
     *
     * @return 存活时间(单位 : 秒)
     */
    int getAge(String optionExpireTime);
}
