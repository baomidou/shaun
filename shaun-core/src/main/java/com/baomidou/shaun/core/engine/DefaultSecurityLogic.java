package com.baomidou.shaun.core.engine;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.isEmpty;
import static org.pac4j.core.util.CommonHelper.isNotEmpty;
import static org.pac4j.core.util.CommonHelper.toNiceString;

import java.util.List;

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
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author miemie
 * @since 2019-07-31
 */
@SuppressWarnings("unchecked")
@Slf4j
@Data
@Accessors(chain = true)
public class DefaultSecurityLogic implements SecurityLogic {

    private ClientFinder clientFinder = new DefaultSecurityClientFinder();

    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    @Override
    public boolean perform(final J2EContext context, final Config config, final String clients,
                           final String authorizers) {
        log.debug("=== SECURITY ===");

        HttpAction action;

        // checks
        assertNotNull("context", context);
        assertNotNull("config", config);
        assertNotNull("clientFinder", clientFinder);
        assertNotNull("authorizationChecker", authorizationChecker);
        final Clients configClients = config.getClients();
        assertNotNull("configClients", configClients);

        // logic
        log.debug("url: {}", context.getFullRequestURL());

        log.debug("clients: {}", clients);
        final List<Client> currentClients = clientFinder.find(configClients, context, clients);
        log.debug("currentClients: {}", currentClients);

        final ProfileManager manager = config.getProfileManagerFactory().apply(context);
        List<CommonProfile> profiles = manager.getAll(true);
        log.debug("profiles: {}", profiles);

        // no profile and some current clients
        if (isEmpty(profiles) && isNotEmpty(currentClients)) {
            boolean updated = false;
            // loop on all clients searching direct ones to perform authentication
            for (final Client currentClient : currentClients) {
                if (currentClient instanceof DirectClient) {
                    log.debug("Performing authentication for direct client: {}", currentClient);

                    final Credentials credentials = currentClient.getCredentials(context);
                    log.debug("credentials: {}", credentials);
                    final CommonProfile profile = currentClient.getUserProfile(credentials, context);
                    log.debug("profile: {}", profile);
                    if (profile != null) {
                        manager.save(true, profile, false);
                        updated = true;
                        break;
                    }
                }
            }
            if (updated) {
                profiles = manager.getAll(true);
                log.debug("new profiles: {}", profiles);
            }
        }

        // we have profile(s) -> check authorizations
        if (isNotEmpty(profiles)) {
            log.debug("authorizers: {}", authorizers);
            if (authorizationChecker.isAuthorized(context, profiles, authorizers, config.getAuthorizers())) {
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
    public String toString() {
        return toNiceString(this.getClass(), "clientFinder", this.clientFinder, "authorizationChecker", this.authorizationChecker,
                "ajaxRequestResolver", this.ajaxRequestResolver);
    }
}
