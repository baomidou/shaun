package com.baomidou.shaun.core.util;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.UserProfile;

/**
 * 前后端分离下使用
 *
 * @author miemie
 * @since 2019-07-30
 */
@SuppressWarnings("unchecked")
public abstract class ProfileHolder {

    private static final String key = Pac4jConstants.USER_PROFILES;

    public static <U extends UserProfile> void save(U profile) {
        save(JEEContextFactory.request(), profile);
    }

    public static <U extends UserProfile> void save(JEEContext context, U profile) {
        context.setRequestAttribute(key, profile);
    }

    public static <U extends UserProfile> void save(HttpServletRequest request, U profile) {
        request.setAttribute(key, profile);
    }

    public static <U extends UserProfile> U get() {
        return get(JEEContextFactory.request());
    }

    public static <U extends UserProfile> U get(JEEContext context) {
        Optional<U> attribute = context.getRequestAttribute(key);
        return attribute.orElse(null);
    }

    public static <U extends UserProfile> U get(HttpServletRequest request) {
        Object attribute = request.getAttribute(key);
        if (attribute != null) {
            return (U) attribute;
        }
        return null;
    }
}
