package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.handler.LogoutHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.profile.ProfileManagerFactory;
import lombok.Data;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;

import java.util.List;

/**
 * 登出 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class LogoutFilter implements ShaunFilter {

    private OnlyPathMatcher pathMatcher;
    private Client client;
    private LogoutHandler logoutHandler;
    private ProfileManagerFactory profileManagerFactory;

    @SuppressWarnings("unchecked")
    @Override
    public boolean goOnChain(J2EContext context) {
        if (pathMatcher.matches(context)) {
            ProfileManager manager = profileManagerFactory.apply(context);
            List<CommonProfile> profiles = manager.getAll(true);
            manager.logout();
            context.getSessionStore().destroySession(context);
            logoutHandler.logout(context, profiles.get(0));
            if (client instanceof IndirectClient) {
                client.redirect(context);
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
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("logoutExecutor", logoutHandler);
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        CommonHelper.assertNotNull("profileManagerFactory", profileManagerFactory);
    }
}
