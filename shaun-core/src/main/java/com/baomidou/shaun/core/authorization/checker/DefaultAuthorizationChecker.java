package com.baomidou.shaun.core.authorization.checker;

import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.CsrfAuthorizer;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * @author miemie
 * @since 2020-05-22
 */
@Slf4j
@SuppressWarnings("all")
public class DefaultAuthorizationChecker implements AuthorizationChecker {

    static final CsrfAuthorizer CSRF_AUTHORIZER = new CsrfAuthorizer();

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles, final String authorizersValue,
                                final Map<String, Authorizer> authorizersMap) {
        final List<Authorizer> authorizers = new ArrayList<>();
        String authorizerNames = authorizersValue;
        // no authorizers defined, we default to CSRF_CHECK
        if (isBlank(authorizerNames)) {
            authorizerNames = DefaultAuthorizers.CSRF_CHECK;
        }
        final String[] names = authorizerNames.split(Pac4jConstants.ELEMENT_SEPARATOR);
        final int nb = names.length;
        for (int i = 0; i < nb; i++) {
            final String name = names[i].trim();
            if (DefaultAuthorizers.CSRF_CHECK.equalsIgnoreCase(name)) {
                authorizers.add(CSRF_AUTHORIZER);
            } else if (!DefaultAuthorizers.NONE.equalsIgnoreCase(name)) {
                // we must have authorizers
                assertNotNull("authorizersMap", authorizersMap);
                Authorizer result = null;
                for (final Map.Entry<String, Authorizer> entry : authorizersMap.entrySet()) {
                    if (areEqualsIgnoreCaseAndTrim(entry.getKey(), name)) {
                        result = entry.getValue();
                        break;
                    }
                }
                // we must have an authorizer defined for this name
                assertNotNull("authorizersMap['" + name + "']", result);
                authorizers.add(result);
            }
        }
        return isAuthorized(context, profiles, authorizers);
    }

    protected boolean isAuthorized(final WebContext context, final List<UserProfile> profiles, final List<Authorizer> authorizers) {
        // authorizations check comes after authentication and profile must not be null nor empty
        assertTrue(isNotEmpty(profiles), "profiles must not be null or empty");
        if (isNotEmpty(authorizers)) {
            // check authorizations using authorizers: all must be satisfied
            for (Authorizer authorizer : authorizers) {
                final boolean isAuthorized = authorizer.isAuthorized(context, profiles);
                log.debug("Checking authorizer: {} -> {}", authorizer, isAuthorized);
                if (!isAuthorized) {
                    return false;
                }
            }
        }
        return true;
    }
}
