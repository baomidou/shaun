package com.baomidou.shaun.core.util;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.UserProfile;

/**
 * 统一的存放登录用户信息
 *
 * @author miemie
 * @since 2019-07-30
 */
@SuppressWarnings("unchecked")
public abstract class ProfileHolder {

    private static final String key_profile = "pac4jUserProfile";
    private static final String key_token = "pac4jUserToken";

    public static <U extends UserProfile> void save(String token, U profile) {
        save(JEEContextFactory.request(), token, profile);
    }

    public static <U extends UserProfile> void save(JEEContext context, String token, U profile) {
        context.setRequestAttribute(key_token, token);
        context.setRequestAttribute(key_profile, profile);
    }

    public static <U extends UserProfile> void save(HttpServletRequest request, String token, U profile) {
        request.setAttribute(key_token, token);
        request.setAttribute(key_profile, profile);
    }

    public static <U extends UserProfile> U getProfile() {
        return getProfile(JEEContextFactory.request());
    }

    public static <U extends UserProfile> U getProfile(JEEContext context) {
        Optional<U> attribute = context.getRequestAttribute(key_profile);
        return attribute.orElse(null);
    }

    public static <U extends UserProfile> U getProfile(HttpServletRequest request) {
        Object attribute = request.getAttribute(key_profile);
        if (attribute != null) {
            return (U) attribute;
        }
        return null;
    }

    public static String getToken() {
        return getToken(JEEContextFactory.request());
    }

    public static String getToken(JEEContext context) {
        Optional<String> attribute = context.getRequestAttribute(key_token);
        return attribute.orElse(null);
    }

    public static String getToken(HttpServletRequest request) {
        Object attribute = request.getAttribute(key_token);
        if (attribute != null) {
            return (String) attribute;
        }
        return null;
    }
}
