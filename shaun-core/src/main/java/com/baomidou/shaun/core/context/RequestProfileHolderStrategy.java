package com.baomidou.shaun.core.context;

import javax.servlet.http.HttpServletRequest;

import org.pac4j.core.util.Pac4jConstants;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * @author miemie
 * @since 2020-05-25
 */
final class RequestProfileHolderStrategy implements ProfileHolderStrategy {

    private static final String key_profile = Pac4jConstants.USER_PROFILES;

    @Override
    public void clearProfile() {
        // do nothing
    }

    @Override
    public TokenProfile getProfile() {
        return (TokenProfile) request().getAttribute(key_profile);
    }

    @Override
    public void setProfile(TokenProfile profile) {
        Assert.notNull(profile, "Only non-null TokenProfile instances are permitted");
        request().setAttribute(key_profile, profile);
    }

    @SuppressWarnings("all")
    private HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
}
