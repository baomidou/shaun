package com.baomidou.shaun.core.util;

import javax.servlet.http.HttpServletRequest;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;

/**
 * 前后端分离下使用
 *
 * @author miemie
 * @since 2019-07-30
 */
@SuppressWarnings("unchecked")
public abstract class ProfileHolder {

    public static <U extends CommonProfile> void setProfile(J2EContext context, U profile) {
        setProfile(context.getRequest(), profile);
    }

    public static <U extends CommonProfile> void setProfile(U profile) {
        setProfile(J2EContextUtil.request(), profile);
    }

    public static <U extends CommonProfile> void setProfile(HttpServletRequest request, U profile) {
        request.setAttribute(Pac4jConstants.USER_PROFILES, profile);
    }

    public static <U extends CommonProfile> U getProfile(J2EContext context) {
        return getProfile(context.getRequest());
    }

    public static <U extends CommonProfile> U getProfile() {
        return getProfile(J2EContextUtil.request());
    }

    public static <U extends CommonProfile> U getProfile(HttpServletRequest request) {
        return (U) request.getAttribute(Pac4jConstants.USER_PROFILES);
    }
}
