package com.baomidou.shaun.core.filter;

import java.util.Collections;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;

import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * security filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class SecurityFilter implements ShaunFilter {

    private final Matcher pathMatcher;

    @Override
    public boolean goOnChain(Config config, JEEContext context) {
        if (pathMatcher.matches(context)) {
            if (log.isDebugEnabled()) {
                log.debug("access security for path : \"{}\" -> \"{}\"", context.getPath(), context.getRequestMethod());
            }
            TokenProfile profile = config.getProfileManager().getProfile(context);
            if (profile != null) {
                if (config.getProfileManager().isAuthorized(profile) &&
                        config.getAuthorizationChecker().isAuthorized(context, Collections.singletonList(profile),
                                config.getAuthorizerNames(), config.getAuthorizersMap())) {
                    ProfileHolder.setProfile(profile);
                    if (log.isDebugEnabled()) {
                        log.debug("authenticated and authorized -> grant access");
                    }
                    return true;
                }
            }
            if (config.isStatelessOrAjax(context)) {
                config.getHttpActionHandler().preHandle(UnauthorizedAction.INSTANCE, context);
                return false;
            } else {
                config.redirectLoginUrl(context);
            }
            return false;
        }
        return true;
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
    }
}
