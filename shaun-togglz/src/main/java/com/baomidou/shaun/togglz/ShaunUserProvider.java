package com.baomidou.shaun.togglz;

import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

import java.util.function.Function;

/**
 * 注意: 只适用于 web_filter 的拦截方式
 *
 * @author miemie
 * @since 2021-01-18
 */
public class ShaunUserProvider implements UserProvider {

    private final CoreConfig config;
    private final String featureAdminRole;
    private final Function<TokenProfile, String> featureUserNameSupplier;

    public ShaunUserProvider(CoreConfig config, String featureAdminRole) {
        this(config, featureAdminRole, TokenProfile::getUsername);
    }

    public ShaunUserProvider(CoreConfig config, String featureAdminRole, Function<TokenProfile, String> featureUserNameSupplier) {
        this.config = config;
        this.featureAdminRole = featureAdminRole;
        this.featureUserNameSupplier = featureUserNameSupplier;
    }

    @Override
    public FeatureUser getCurrentUser() {
        TokenProfile profile = ProfileHolder.getProfile();
        if (profile != null) {
            AuthorityManager manager = config.getAuthorityManager();
            boolean featureAdmin = manager.isSkipAuthentication(profile) || manager.roles(profile).contains(featureAdminRole);
            return new SimpleFeatureUser(featureUserNameSupplier.apply(profile), featureAdmin);
        }
        // user is not authenticated
        return null;
    }
}
