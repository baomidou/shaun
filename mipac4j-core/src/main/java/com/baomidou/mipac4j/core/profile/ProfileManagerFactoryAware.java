package com.baomidou.mipac4j.core.profile;

import com.baomidou.mipac4j.core.config.Config;
import lombok.Getter;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;

/**
 * @author miemie
 * @since 2019-07-25
 */
@Getter
public class ProfileManagerFactoryAware {

    private ProfileManagerFactory profileManagerFactory;

    /**
     * Given a webcontext generate a profileManager for it.
     * Can be overridden for custom profile manager implementations
     *
     * @param context the web context
     * @param config  the configuration
     * @return profile manager implementation built from the context
     */
    protected ProfileManager getProfileManager(final J2EContext context, final Config config) {
        ProfileManagerFactory configProfileManagerFactory = config.getProfileManagerFactory();
        if (configProfileManagerFactory != null) {
            return configProfileManagerFactory.apply(context);
        } else if (profileManagerFactory != null) {
            return profileManagerFactory.apply(context);
        } else {
            return ProfileManagerFactory.DEFAULT.apply(context);
        }
    }

    public void setProfileManagerFactory(final ProfileManagerFactory factory) {
        CommonHelper.assertNotNull("factory", factory);
        this.profileManagerFactory = factory;
    }
}
