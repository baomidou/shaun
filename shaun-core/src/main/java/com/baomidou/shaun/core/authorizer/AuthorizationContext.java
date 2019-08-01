package com.baomidou.shaun.core.authorizer;

import java.util.Set;

import org.pac4j.core.profile.UserProfile;

/**
 * @author miemie
 * @since 2019-08-01
 */
public interface AuthorizationContext<U extends UserProfile> {

    default Set<String> roles(U profile) {
        return profile.getRoles();
    }

    default Set<String> permissions(U profile) {
        return profile.getPermissions();
    }
}
