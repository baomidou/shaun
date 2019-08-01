package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.util.ProfileHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultSecurityClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.Collections;
import java.util.List;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.isNotEmpty;

/**
 * 安全 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@SuppressWarnings("unchecked")
@Slf4j
@Data
public class SecurityFilter implements ShaunFilter {

    private ClientFinder clientFinder = new DefaultSecurityClientFinder();
    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();
    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
    private Config config;
    private String authorizers;
    private PathMatcher pathMatcher;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (pathMatcher.matches(context)) {
            log.debug("=== SECURITY ===");

            HttpAction action;

            // checks
            assertNotNull("config", config);
            assertNotNull("clientFinder", clientFinder);
            assertNotNull("authorizationChecker", authorizationChecker);
            final Clients configClients = config.getClients();
            assertNotNull("configClients", configClients);

            // logic
            log.debug("url: {}", context.getFullRequestURL());

            final List<Client> currentClients = clientFinder.find(configClients, context, null);
            log.debug("currentClients: {}", currentClients);

            CommonProfile profile = ProfileHolder.get(context, true);
            log.debug("profile: {}", profile);

            // no profile and some current clients
            if (profile == null && isNotEmpty(currentClients)) {
                boolean updated = false;
                // loop on all clients searching direct ones to perform authentication
                for (final Client currentClient : currentClients) {
                    if (currentClient instanceof DirectClient) {
                        log.debug("Performing authentication for direct client: {}", currentClient);

                        final Credentials credentials = currentClient.getCredentials(context);
                        log.debug("credentials: {}", credentials);
                        profile = currentClient.getUserProfile(credentials, context);
                        log.debug("profile: {}", profile);
                        if (profile != null) {
                            ProfileHolder.save(context, profile, true);
                            updated = true;
                            break;
                        }
                    }
                }
                if (updated) {
                    profile = ProfileHolder.get(context, true);
                    log.debug("new profiles: {}", profile);
                }
            }

            // we have profile(s) -> check authorizations
            if (profile != null) {
                log.debug("authorizers: {}", authorizers);
                if (authorizationChecker.isAuthorized(context, Collections.singletonList(profile),
                        authorizers, config.getAuthorizers())) {
                    log.debug("authenticated and authorized -> grant access");
                    return true;
                } else {
                    log.debug("forbidden");
                    action = HttpAction.forbidden(context);
                }
            } else {
                if (startAuthentication(context, currentClients)) {
                    log.debug("Starting authentication");
                    action = redirectToIdentityProvider(context, currentClients);
                } else {
                    log.debug("unauthorized");
                    action = HttpAction.unauthorized(context);
                }
            }
            if (action.getCode() == HttpConstants.TEMP_REDIRECT) {
                return false;
            }
            throw action;
        }
        return true;
    }

    /**
     * Return whether we must start a login process if the first client is an indirect one.
     *
     * @param context        the web context
     * @param currentClients the current clients
     * @return whether we must start a login process
     */
    private boolean startAuthentication(final J2EContext context, final List<Client> currentClients) {
        return isNotEmpty(currentClients) && currentClients.get(0) instanceof IndirectClient;
    }

    /**
     * Perform a redirection to start the login process of the first indirect client.
     *
     * @param context        the web context
     * @param currentClients the current clients
     * @return the performed redirection
     */
    private HttpAction redirectToIdentityProvider(final J2EContext context, final List<Client> currentClients) {
        final IndirectClient currentClient = (IndirectClient) currentClients.get(0);
        return currentClient.redirect(context);
    }

    @Override
    public int order() {
        return 300;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("config", config);
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
    }
}
