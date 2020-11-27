package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.ShaunConfig;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.profile.TokenProfile;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class LogoutFilter implements ShaunFilter {

    private final Matcher pathMatcher;
    private SecurityManager securityManager;

    @Override
    public boolean goOnChain(ShaunConfig config, JEEContext context) {
        if (pathMatcher.matches(context)) {
            final TokenProfile profile = ProfileHolder.getProfile();
            securityManager.logout(profile);
            logoutThen(config, context);
            return false;
        }
        return true;
    }

    protected void logoutThen(ShaunConfig config, JEEContext context) {
        if (config.isStateless()) {
            return;
        }
        if (!config.getAjaxRequestResolver().isAjax(context)) {
            config.redirectLoginUrl(context);
        }
    }

    @Override
    public int order() {
        return 300;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        CommonHelper.assertNotNull("securityManager", securityManager);
    }
}
