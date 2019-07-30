package com.baomidou.mipac4j.core.filter.stateless;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;
import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;
import com.baomidou.mipac4j.core.util.ProfileHolder;

import lombok.Data;

/**
 * 登出 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@SuppressWarnings("unchecked")
@Data
public class StatelessLogoutFilter implements Pac4jFilter {

    private OnlyPathMatcher pathMatcher;
    private LogoutExecutor logoutExecutor;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (pathMatcher.matches(context)) {
            final CommonProfile profile = ProfileHolder.getProfile(context);
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
