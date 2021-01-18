package com.baomidou.shaun.ff4j;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;
import org.ff4j.security.AbstractAuthorizationManager;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

/**
 * @author miemie
 * @since 2021-01-18
 */
public class ShaunAuthorisationManager extends AbstractAuthorizationManager {

    private final CoreConfig config;
    private final Function<TokenProfile, String> currentUserNameSupplier;

    public ShaunAuthorisationManager(CoreConfig config) {
        this(config, TokenProfile::getUsername);
    }

    public ShaunAuthorisationManager(CoreConfig config, Function<TokenProfile, String> currentUserNameSupplier) {
        this.config = config;
        this.currentUserNameSupplier = currentUserNameSupplier;
    }

    @Override
    public String getCurrentUserName() {
        TokenProfile profile = ProfileHolder.getProfile();
        if (profile != null) {
            return currentUserNameSupplier.apply(profile);
        }
        return "anonymous";
    }

    @Override
    public Set<String> getCurrentUserPermissions() {
        TokenProfile profile = ProfileHolder.getProfile();
        if (profile != null) {
            return config.getAuthorityManager().permissions(profile);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> listAllPermissions() {
        return getCurrentUserPermissions();
    }
}
