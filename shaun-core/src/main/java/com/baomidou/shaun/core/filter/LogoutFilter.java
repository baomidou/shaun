package com.baomidou.shaun.core.filter;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import com.baomidou.shaun.core.context.GlobalConfig;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.util.ProfileHolder;

import lombok.Data;

/**
 * 登出 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@SuppressWarnings("unchecked")
@Data
public class LogoutFilter implements ShaunFilter {

    private OnlyPathMatcher pathMatcher;
    private LogoutHandler logoutExecutor;

    @Override
    public boolean goOnChain(JEEContext context) {
        if (pathMatcher.matches(context)) {
            final UserProfile profile = ProfileHolder.get(context);
            logoutExecutor.logout(profile);
            if (!GlobalConfig.isStatelessOrAjax(context)) {
                GlobalConfig.gotoLoginUrl(context);
            }
            return false;
        }
        return true;
    }

    @Override
    public int order() {
        return 300;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        CommonHelper.assertNotNull("logoutExecutor", logoutExecutor);
    }
}
