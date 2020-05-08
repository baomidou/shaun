package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.ProfileHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.matching.checker.DefaultMatchingChecker;
import org.pac4j.core.matching.checker.MatchingChecker;
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
public class SecurityFilter implements ShaunFilter {

    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();
    private MatchingChecker matchingChecker = new DefaultMatchingChecker();
    private Matcher pathMatcher;
    private TokenClient tokenClient;

    public SecurityFilter(Matcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    @Override
    public boolean goOnChain(Config config, JEEContext context) {
        if (pathMatcher.matches(context)) {
            log.debug("=== SECURITY ===");

            final Optional<TokenCredentials> credentials = tokenClient.getCredentials(context);
            log.debug("credentials: {}", credentials);
            if (credentials.isPresent()) {
                final Optional<UserProfile> profile = tokenClient.getUserProfile(credentials.get(), context);
                log.debug("profile: {}", profile);
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
                    if (authorizationChecker.isAuthorized(context, Collections.singletonList(tokenProfile),
                            config.getAuthorizerNames(), config.getAuthorizersMap())) {
                        ProfileHolder.save(context, tokenProfile.setToken(credentials.get().getToken()));
                        log.debug("authenticated and authorized -> grant access");
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
        CommonHelper.assertNotNull("tokenClient", tokenClient);
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
    }
}
