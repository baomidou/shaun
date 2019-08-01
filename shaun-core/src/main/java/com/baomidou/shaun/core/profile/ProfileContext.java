package com.baomidou.shaun.core.profile;

import com.baomidou.shaun.core.context.JEEContextFactory;
import com.baomidou.shaun.core.util.JEEContextUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

/**
 * 非前后分离下使用
 *
 * @author miemie
 * @since 2019-07-31
 */
@SuppressWarnings("unchecked")
@Data
@AllArgsConstructor
public class ProfileContext {

    private final ProfileManagerFactory profileManagerFactory;
    private final SessionStore sessionStore;
    private final JEEContextFactory jeeContextFactory;

    public <U extends CommonProfile> void login(U profile) {
        JEEContext context = JEEContextUtil.getJEEContext(jeeContextFactory, sessionStore);
        ProfileManager manager = getProfileManager(context);
        manager.save(true, profile, false);
    }

    public <U extends CommonProfile> ProfileManager<U> getProfileManager(JEEContext context) {
        return profileManagerFactory.apply(context);
    }

    public void logout() {
        logout(JEEContextUtil.getJEEContext(jeeContextFactory, sessionStore));
    }

    public void logout(JEEContext context) {
        ProfileManager manager = profileManagerFactory.apply(context);
        manager.logout();
        context.getSessionStore().destroySession(context);
    }
}
