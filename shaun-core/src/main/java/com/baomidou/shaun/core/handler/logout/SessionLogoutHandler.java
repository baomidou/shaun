package com.baomidou.shaun.core.handler.logout;

import com.baomidou.shaun.core.profile.ProfileManagerFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

/**
 * @author miemie
 * @since 2019-07-31
 */
@Data
@AllArgsConstructor
public class SessionLogoutHandler implements LogoutHandler<CommonProfile> {

    private final ProfileManagerFactory profileManagerFactory;
    private final SessionStore<J2EContext> sessionStore;
    private final IndirectClient client;

    @Override
    public void logout(J2EContext context, CommonProfile profile) {
        ProfileManager manager = profileManagerFactory.apply(context);
        manager.logout();
        sessionStore.destroySession(context);
        client.redirect(context); // 不抛出这个异常
    }
}
