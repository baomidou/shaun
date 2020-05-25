package com.baomidou.shaun.core.context;

import org.springframework.util.Assert;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * @author miemie
 * @since 2020-05-25
 */
final class InheritableThreadLocalProfileHolderStrategy implements ProfileHolderStrategy {

    private static final ThreadLocal<TokenProfile> contextHolder = new InheritableThreadLocal<>();

    @Override
    public void clearProfile() {
        contextHolder.remove();
    }

    @Override
    public TokenProfile getProfile() {
        return contextHolder.get();
    }

    @Override
    public void setProfile(TokenProfile profile) {
        Assert.notNull(profile, "Only non-null TokenProfile instances are permitted");
        contextHolder.set(profile);
    }
}
