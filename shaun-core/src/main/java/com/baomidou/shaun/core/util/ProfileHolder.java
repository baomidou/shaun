package com.baomidou.shaun.core.util;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.util.Pac4jConstants;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * 统一的存放登录用户信息
 *
 * @author miemie
 * @since 2019-07-30
 */
@SuppressWarnings("unchecked")
public abstract class ProfileHolder {

    private static final String key_profile = Pac4jConstants.USER_PROFILES;

    public static void save(TokenProfile profile) {
        save(JEEContextFactory.request(), profile);
    }

    public static void save(JEEContext context, TokenProfile profile) {
        context.setRequestAttribute(key_profile, profile);
    }

    public static void save(HttpServletRequest request, TokenProfile profile) {
        request.setAttribute(key_profile, profile);
    }

    public static TokenProfile getProfile() {
        return getProfile(JEEContextFactory.request());
    }

    public static TokenProfile getProfile(JEEContext context) {
        Optional<TokenProfile> attribute = context.getRequestAttribute(key_profile);
        return attribute.orElse(null);
    }

    public static TokenProfile getProfile(HttpServletRequest request) {
        Object attribute = request.getAttribute(key_profile);
        if (attribute != null) {
            return (TokenProfile) attribute;
        }
        return null;
    }

    public static String getToken() {
        return getToken(JEEContextFactory.request());
    }

    public static String getToken(JEEContext context) {
        TokenProfile profile = getProfile(context);
        if (profile != null) {
            return profile.getToken();
        }
        return null;
    }

    public static String getToken(HttpServletRequest request) {
        TokenProfile profile = getProfile(request);
        if (profile != null) {
            return profile.getToken();
        }
        return null;
    }
}
