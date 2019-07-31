package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.engine.LogoutExecutor;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.profile.ProfileManagerFactory;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.matching.Matcher;
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

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Matcher matcher;

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
    private LogoutExecutor logoutExecutor;
    private String logoutUrl;
    private Client client;
    private ProfileManagerFactory profileManagerFactory;

    @SuppressWarnings("unchecked")
    @Override
    public boolean goOnChain(J2EContext context) {
        if (matcher.matches(context)) {
            ProfileManager manager = profileManagerFactory.apply(context);
            List<CommonProfile> profiles = manager.getAll(true);
            manager.logout();
            context.getSessionStore().destroySession(context);
            logoutExecutor.logout(context, profiles.get(0));
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
        CommonHelper.assertNotBlank("logoutUrl", logoutUrl);
        CommonHelper.assertNotNull("logoutExecutor", logoutExecutor);
        this.matcher = new OnlyPathMatcher(logoutUrl);
    }
}
