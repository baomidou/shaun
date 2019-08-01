package com.baomidou.shaun.core.util;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.UserProfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * 前后端分离下使用
 *
 * @author miemie
 * @since 2019-07-30
 */
@SuppressWarnings("unchecked")
public abstract class ProfileHolder {

    private static final String key = Pac4jConstants.USER_PROFILES;

    public static <U extends UserProfile> void save(U profile, boolean saveInSession) {
        save(JEEContextUtil.request(), profile, saveInSession);
    }

    public static <U extends UserProfile> void save(JEEContext context, U profile, boolean saveInSession) {
        context.setRequestAttribute(key, profile);
        if (saveInSession) {
            context.getSessionStore().set(context, key, profile);
        }
    }

    public static <U extends UserProfile> void save(HttpServletRequest request, U profile, boolean saveInSession) {
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

    public static <U extends UserProfile> U get(boolean readFromSession) {
        return get(JEEContextUtil.request(), readFromSession);
    }

    public static <U extends UserProfile> U get(JEEContext context, boolean readFromSession) {
        Optional<U> attribute = context.getRequestAttribute(key);
        if (attribute.isPresent()) {
            return attribute.get();
        } else {
            if (readFromSession) {
                Optional<U> sessionA = context.getSessionStore().get(context, key);
                if (sessionA.isPresent()) {
                    return sessionA.get();
                }
            }
        }
        return null;
    }

    public static <U extends UserProfile> U get(HttpServletRequest request, boolean readFromSession) {
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
