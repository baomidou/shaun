/*
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.core.context.session;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.SessionStore;

import java.util.Optional;

/**
 * 不用 session(替换掉 pac4j 提供的)
 *
 * @author miemie
 * @since 2019-08-03
 */
public class NoneSessionStore implements SessionStore<JEEContext> {

    public static final NoneSessionStore INSTANCE = new NoneSessionStore();

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
        return true;
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
