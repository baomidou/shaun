package com.baomidou.mipac4j.core.filter.stateless;

import com.baomidou.mipac4j.core.client.TokenClient;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;
import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;
import lombok.Data;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * 登出 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class StatelessLogoutFilter implements Pac4jFilter {

    private OnlyPathMatcher pathMatcher;
    private LogoutExecutor logoutExecutor;
    private TokenClient tokenClient;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (pathMatcher.matches(context)) {
            final TokenCredentials credentials = tokenClient.getCredentials(context);
            final CommonProfile profile = tokenClient.getUserProfile(credentials, context);
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
        CommonHelper.assertNotNull("tokenClient", tokenClient);
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        CommonHelper.assertNotNull("logoutExecutor", logoutExecutor);
    }
}
