package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.profile.TokenProfile;
import org.pac4j.core.context.JEEContext;

/**
 * profile 管理器
 *
 * @author miemie
 * @since 2020-09-04
 */
public interface ProfileTokenManager {

    /**
     * 从上下文中获取到 TokenProfile
     *
     * @param context JEEContext
     * @return TokenProfile
     */
    TokenProfile getProfile(JEEContext context);

    /**
     * 把 TokenProfile 构建为 jwt
     *
     * @param profile          TokenProfile
     * @param optionExpireTime 超时时间
     * @return jwt
     */
    String generateJwt(TokenProfile profile, String optionExpireTime);
}
