package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

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

            final Optional<TokenCredentials> credentials = config.getTokenClient().getCredentials(context);
            if (log.isDebugEnabled()) {
                log.debug("credentials: {}", credentials);
            }
            if (credentials.isPresent()) {
                final Optional<UserProfile> profile = config.getTokenClient().getUserProfile(credentials.get(), context);
                if (log.isDebugEnabled()) {
                    log.debug("profile: {}", profile);
                }
                if (profile.isPresent()) {
                    // todo 兼容性升级
                    CommonProfile commonProfile = (CommonProfile) profile.get();
                    TokenProfile tokenProfile;
                    if (commonProfile instanceof TokenProfile) {
                        tokenProfile = (TokenProfile) commonProfile;
                    } else {
                        tokenProfile = new TokenProfile();
                        Set<String> permissions = commonProfile.getPermissions();
                        tokenProfile.addPermissions(permissions);
                        Set<String> roles = commonProfile.getRoles();
                        tokenProfile.addRoles(roles);
                        tokenProfile.setId(commonProfile.getId());
                        tokenProfile.addAttributes(commonProfile.getAttributes());
                    }
                    // todo 兼容性升级
                    if (config.getAuthorizationChecker().isAuthorized(context, Collections.singletonList(tokenProfile),
                            config.getAuthorizerNames(), config.getAuthorizersMap())) {
                        ProfileHolder.setProfile(tokenProfile.setToken(credentials.get().getToken()));
                        if (log.isDebugEnabled()) {
                            log.debug("authenticated and authorized -> grant access");
                        }
                        return true;
                    }
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
