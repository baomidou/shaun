package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.ProfileHolder;
import lombok.Data;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;

/**
 * logout filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class LogoutFilter implements ShaunFilter {

    private Matcher pathMatcher;
    private LogoutHandler logoutExecutor;

    public LogoutFilter(Matcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    @Override
    public boolean goOnChain(Config config, JEEContext context) {
        if (pathMatcher.matches(context)) {
            final TokenProfile profile = ProfileHolder.getProfile(context);
            logoutExecutor.logout(profile);
            if (!config.isStatelessOrAjax(context)) {
                config.redirectLoginUrl(context);
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
