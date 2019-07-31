package com.baomidou.shaun.core.profile;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

import com.baomidou.shaun.core.context.J2EContextFactory;
import com.baomidou.shaun.core.util.J2EContextUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

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
    private final J2EContextFactory j2EContextFactory;

    public <U extends CommonProfile> void login(U profile) {
        J2EContext context = J2EContextUtil.getJ2EContext(j2EContextFactory, sessionStore);
        ProfileManager manager = getProfileManager(context);
        manager.save(true, profile, false);
    }

    public <U extends CommonProfile> ProfileManager<U> getProfileManager(J2EContext context) {
        return profileManagerFactory.apply(context);
    }

    public void logout() {
        logout(J2EContextUtil.getJ2EContext(j2EContextFactory, sessionStore));
    }

    public void logout(J2EContext context) {
        ProfileManager manager = profileManagerFactory.apply(context);
        manager.logout();
        context.getSessionStore().destroySession(context);
    }
}
