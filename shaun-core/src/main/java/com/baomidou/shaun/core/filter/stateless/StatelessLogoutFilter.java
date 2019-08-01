package com.baomidou.shaun.core.filter.stateless;

import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.handler.logout.LogoutHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.util.ProfileHolder;
import lombok.Data;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * 登出 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@SuppressWarnings("unchecked")
@Data
public class StatelessLogoutFilter implements ShaunFilter {

    private OnlyPathMatcher pathMatcher;
    private LogoutHandler logoutExecutor;

    @Override
    public boolean goOnChain(JEEContext context) {
        if (pathMatcher.matches(context)) {
            final CommonProfile profile = ProfileHolder.get(context, false);
            logoutExecutor.logout(context, profile);
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
