package com.baomidou.shaun.core.authorizer;

import org.pac4j.core.profile.UserProfile;

/**
 * @author miemie
 * @since 2019-08-02
 */
public interface Authorizer<U extends UserProfile> {

    boolean isAuthorized(U profile);
}