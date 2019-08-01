package com.baomidou.shaun.core.util;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 前后端分离下使用
 *
 * @author miemie
 * @since 2019-07-30
 */
@SuppressWarnings("unchecked")
public abstract class ProfileHolder {

    private static final String key = Pac4jConstants.USER_PROFILES;

    public static <U extends CommonProfile> void save(U profile, boolean saveInSession) {
        save(JEEContextUtil.request(), profile, saveInSession);
    }

    public static <U extends CommonProfile> void save(JEEContext context, U profile, boolean saveInSession) {
        context.setRequestAttribute(key, profile);
        if (saveInSession) {
            context.getSessionStore().set(context, key, profile);
        }
    }

    public static <U extends CommonProfile> void save(HttpServletRequest request, U profile, boolean saveInSession) {
        request.setAttribute(key, profile);
        if (saveInSession) {
            HttpSession session = request.getSession();
            if (profile == null) {
                session.removeAttribute(key);
            } else {
                session.setAttribute(key, profile);
            }
        }
    }

    public static <U extends CommonProfile> U get(boolean readFromSession) {
        return get(JEEContextUtil.request(), readFromSession);
    }

    public static <U extends CommonProfile> U get(JEEContext context, boolean readFromSession) {
        Object attribute = context.getRequestAttribute(key);
        if (attribute != null) {
            return (U) attribute;
        } else {
            if (readFromSession) {
                return (U) context.getSessionStore().get(context, key);
            }
            return null;
        }
    }

    public static <U extends CommonProfile> U get(HttpServletRequest request, boolean readFromSession) {
        Object attribute = request.getAttribute(key);
        if (attribute != null) {
            return (U) attribute;
        } else {
            if (readFromSession) {
                return (U) request.getSession().getAttribute(key);
            }
            return null;
        }
    }
}
