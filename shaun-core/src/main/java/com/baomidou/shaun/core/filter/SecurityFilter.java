package com.baomidou.shaun.core.filter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.context.GlobalConfig;
import com.baomidou.shaun.core.handler.HttpActionHandler;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.ProfileHolder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
    private Matcher pathMatcher;
    private TokenClient tokenClient;
    private String authorizers;
    private Map<String, Authorizer> authorizerMap;
    private HttpActionHandler httpActionHandler;

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
                    log.debug("authorizers: {}", authorizers);
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
                            authorizers, authorizerMap)) {
                        ProfileHolder.save(context, tokenProfile.setToken(credentials.get().getToken()));
                        log.debug("authenticated and authorized -> grant access");
                        return true;
                    }
                }
            }
            if (GlobalConfig.isStatelessOrAjax(context)) {
                httpActionHandler.preHandle(UnauthorizedAction.INSTANCE, context);
                return false;
            } else {
                GlobalConfig.gotoLoginUrl(context);
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
        CommonHelper.assertNotNull("httpActionHandler", httpActionHandler);
    }
}
