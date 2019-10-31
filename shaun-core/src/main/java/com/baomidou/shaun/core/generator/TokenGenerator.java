package com.baomidou.shaun.core.generator;

import org.pac4j.core.profile.CommonProfile;

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
     * @param <U>                      泛型
     * @return token
     */
    <U extends CommonProfile> String generate(final U profile, final boolean isSkipAuthenticationUser);

    /**
     * 获取存活时间
     *
     * @return 存活时间(单位 : 秒)
     */
    Integer getAge();
}
