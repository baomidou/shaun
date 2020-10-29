package com.baomidou.shaun.core.mgt;

import org.pac4j.core.context.JEEContext;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * profile 管理器
 *
 * @author miemie
 * @since 2020-09-04
 */
public interface ProfileManager {

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

    /**
     * login 后置操作
     * <p>
     * 可以在这里把用户信息存储进外部(比如redis)
     *
     * @param profile 登陆用户
     */
    default void afterLogin(TokenProfile profile) {
        // do nothing
    }

    /**
     * 访问需要登录的资源之前进行验证是否允许访问
     * 只适合判断该用户的登录信息是否有效
     * <p>
     * 可以在这里从外部(比如redis)读取用户判断是否允许访问
     *
     * @param profile 登陆用户
     * @return 是否允许访问
     */
    default boolean isAuthorized(TokenProfile profile) {
        return true;
    }

    /**
     * logout 后置操作
     * <p>
     * 可以在这里把用户信息从外部存储(比如redis)上删除
     *
     * @param profile 登陆用户
     */
    default void afterLogout(TokenProfile profile) {
        // do nothing
    }
}
