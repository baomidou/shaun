package com.baomidou.shaun.core.handler;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * 登出执行器
 *
 * @author miemie
 * @since 2019-07-22
 */
@FunctionalInterface
public interface LogoutHandler<U extends CommonProfile> {

    LogoutHandler DO_NOTHING = (ctx, pf) -> {
    };

    void logout(WebContext context, U profile);
}
