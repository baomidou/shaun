package com.baomidou.shaun.stateless.filter;

import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.util.ProfileHolder;
import com.baomidou.shaun.stateless.client.TokenClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 前后分离的安全 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Slf4j
@Data
public class SecurityFilter implements ShaunFilter {

    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();
    private PathMatcher pathMatcher;
    private TokenClient tokenClient;
    private String authorizers;
    private Map<String, Authorizer> authorizerMap;

    @Override
    public boolean goOnChain(JEEContext context) {
        if (pathMatcher.matches(context)) {
            log.debug("=== SECURITY ===");

            final Optional<TokenCredentials> credentials = tokenClient.getCredentials(context);
            log.debug("credentials: {}", credentials);

            if (credentials.isPresent()) {
                final Optional<UserProfile> profile = tokenClient.getUserProfile(credentials.get(), context);
                log.debug("profile: {}", profile);

                if (profile.isPresent()) {
                    ProfileHolder.save(context, profile.get(), false);

                    log.debug("authorizers: {}", authorizers);
                    if (authorizationChecker.isAuthorized(context, Collections.singletonList(profile.get()),
                            authorizers, authorizerMap)) {
                        log.debug("authenticated and authorized -> grant access");
                        return true;
                    } else {
                        log.debug("forbidden");
                        throw ForbiddenAction.INSTANCE;
                    }
                }
            } else {
                throw UnauthorizedAction.INSTANCE;
            }
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
