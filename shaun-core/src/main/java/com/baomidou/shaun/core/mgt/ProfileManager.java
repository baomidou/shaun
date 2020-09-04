package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * profile 管理器
 *
 * @author miemie
 * @since 2020-09-04
 */
public interface ProfileManager {

    /**
     * login 后置操作
     * <p>
     * 可以在这里把用户信息存储进外部(比如redis)
     *
     * @param profile 登陆用户
     */
    void afterLogin(TokenProfile profile);

    /**
     * 访问需要登录的资源之前进行验证是否允许访问
     * 只适合判断该用户的登录信息是否有效
     * <p>
     * 可以在这里从外部(比如redis)读取用户判断是否允许访问
     *
     * @param profile 登陆用户
     * @return 是否允许访问
     */
    boolean isAuthorized(TokenProfile profile);

    /**
     * logout 后置操作
     * <p>
     * 可以在这里把用户信息从外部存储(比如redis)上删除
     *
     * @param profile 登陆用户
     */
    void afterLogout(TokenProfile profile);
}
