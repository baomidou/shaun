package com.baomidou.mipac4j.core.context;

import java.util.function.Function;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;

/**
 * 包装一下
 *
 * @author miemie
 * @since 2019-07-24
 */
public interface ProfileManagerFactory extends Function<WebContext, ProfileManager> {

    ProfileManagerFactory DEFAULT = ProfileManager::new;
}
