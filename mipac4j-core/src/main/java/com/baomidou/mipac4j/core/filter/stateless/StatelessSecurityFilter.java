package com.baomidou.mipac4j.core.filter.stateless;

import java.util.Collections;
import java.util.Map;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import com.baomidou.mipac4j.core.client.TokenClient;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;
import com.baomidou.mipac4j.core.util.ProfileHolder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 前后分离的安全 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Slf4j
@Data
public class StatelessSecurityFilter implements Pac4jFilter {

    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();
    private PathMatcher pathMatcher;
    private TokenClient tokenClient;
    private String authorizers;
    private Map<String, Authorizer> authorizerMap;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (pathMatcher.matches(context)) {
            log.debug("=== SECURITY ===");

            final TokenCredentials credentials = tokenClient.getCredentials(context);
            log.debug("credentials: {}", credentials);
            final CommonProfile profile = tokenClient.getUserProfile(credentials, context);
            log.debug("profile: {}", profile);

            HttpAction action;
            if (profile != null) {
                ProfileHolder.setProfile(context, profile);
                if (authorizationChecker.isAuthorized(context, Collections.singletonList(profile), authorizers, authorizerMap)) {
                    log.debug("authenticated and authorized -> grant access");
                    return true;
                } else {
                    log.debug("forbidden");
                    action = HttpAction.forbidden(context);
                }
            } else {
                action = HttpAction.unauthorized(context);
            }
            throw action;
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
