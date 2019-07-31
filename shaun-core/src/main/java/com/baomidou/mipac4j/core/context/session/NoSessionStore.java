package com.baomidou.mipac4j.core.context.session;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * 不进行 session 存储
 *
 * @author miemie
 * @since 2019-07-25
 */
public class NoSessionStore implements SessionStore<J2EContext> {

    public static final NoSessionStore INSTANCE = new NoSessionStore();

    @Override
    public String getOrCreateSessionId(J2EContext context) {
        return null;
    }

    @Override
    public Object get(J2EContext context, String key) {
        return null;
    }

    @Override
    public void set(J2EContext context, String key, Object value) {

    }

    @Override
    public boolean destroySession(J2EContext context) {
        return false;
    }

    @Override
    public Object getTrackableSession(J2EContext context) {
        return null;
    }

    @Override
    public SessionStore<J2EContext> buildFromTrackableSession(J2EContext context, Object trackableSession) {
        return null;
    }

    @Override
    public boolean renewSession(J2EContext context) {
        return false;
    }
}
