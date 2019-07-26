package com.baomidou.mipac4j.core.engine;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.Optional;

/**
 * 登出执行器
 *
 * @author miemie
 * @since 2019-07-22
 */
@FunctionalInterface
public interface LogoutExecutor<U extends CommonProfile> {

    LogoutExecutor DO_NOTHING = (ctx, pf) -> {
    };

    void logout(WebContext context, Optional<U> profile);
}
