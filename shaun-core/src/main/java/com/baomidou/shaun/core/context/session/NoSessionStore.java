package com.baomidou.shaun.core.context.session;

import java.util.Optional;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * 不用 session(替换掉 pac4j 提供的)
 *
 * @author miemie
 * @since 2019-08-03
 */
public class NoSessionStore implements SessionStore<JEEContext> {

    public static final NoSessionStore INSTANCE = new NoSessionStore();

    @Override
    public String getOrCreateSessionId(JEEContext context) {
        return null;
    }

    @Override
    public Optional<Object> get(JEEContext context, String key) {
        return Optional.empty();
    }

    @Override
    public void set(JEEContext context, String key, Object value) {

    }

    @Override
    public boolean destroySession(JEEContext context) {
        return false;
    }

    @Override
    public Optional<?> getTrackableSession(JEEContext context) {
        return Optional.empty();
    }

    @Override
    public Optional<SessionStore<JEEContext>> buildFromTrackableSession(JEEContext context, Object trackableSession) {
        return Optional.empty();
    }

    @Override
    public boolean renewSession(JEEContext context) {
        return false;
    }
}
