package com.baomidou.mipac4j.core.engine;

import com.baomidou.mipac4j.core.config.Config;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultSecurityClientFinder;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.engine.decision.DefaultProfileStorageDecision;
import org.pac4j.core.engine.decision.ProfileStorageDecision;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

import java.util.List;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * @author miemie
 * @since 2019-07-25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SecurityLogic extends AbstractExceptionAwareLogic {

    private ClientFinder clientFinder = new DefaultSecurityClientFinder();

    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();

    private ProfileStorageDecision profileStorageDecision = new DefaultProfileStorageDecision();

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    @SuppressWarnings("unchecked")
    public Boolean perform(final J2EContext context, final Config config, final String clients) {

        log.debug("=== SECURITY ===");

        try {

            // checks
            assertNotNull("context", context);
            assertNotNull("config", config);
            assertNotNull("clientFinder", clientFinder);
            assertNotNull("authorizationChecker", authorizationChecker);
            assertNotNull("profileStorageDecision", profileStorageDecision);
            final Clients configClients = config.getClients();
            assertNotNull("configClients", configClients);

            // logic
            log.debug("url: {}", context.getFullRequestURL());

            log.debug("clients: {}", clients);
            final List<Client> currentClients = clientFinder.find(configClients, context, clients);
            log.debug("currentClients: {}", currentClients);

            final boolean loadProfilesFromSession = profileStorageDecision.mustLoadProfilesFromSession(context, currentClients);
            log.debug("loadProfilesFromSession: {}", loadProfilesFromSession);
            final ProfileManager manager = getProfileManager(context, config);
            List<CommonProfile> profiles = manager.getAll(loadProfilesFromSession);
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
                            final boolean saveProfileInSession = profileStorageDecision.mustSaveProfileInSession(context,
                                    currentClients, (DirectClient) currentClient, profile);
                            log.debug("saveProfileInSession: {} / multiProfile: {}", saveProfileInSession, false);
                            manager.save(saveProfileInSession, profile, false);
                            updated = true;
                            break;
                        }
                    }
                }
                if (updated) {
                    profiles = manager.getAll(loadProfilesFromSession);
                    log.debug("new profiles: {}", profiles);
                }
            }

            // we have profile(s) -> check authorizations
            if (isNotEmpty(profiles)) {
                log.debug("authorizers: {}", config.getAuthorizes());
                if (authorizationChecker.isAuthorized(context, profiles, config.getAuthorizes(), config.getAuthorizeMap())) {
                    log.debug("authenticated and authorized -> grant access");
                    return true;
                } else {
                    log.debug("forbidden");
                    forbidden(context, profiles, config.getAuthorizes());
                }
            } else {
                if (startAuthentication(context, currentClients)) {
                    log.debug("Starting authentication");
                    saveRequestedUrl(context, currentClients);
                    redirectToIdentityProvider(context, currentClients);
                } else {
                    log.debug("unauthorized");
                    unauthorized(context, currentClients);
                }
            }
        } catch (final Exception e) {
            return handleException(e, context);
        }

        return false;
    }

    /**
     * Return a forbidden error.
     *
     * @param context     the web context
     * @param profiles    the current profiles
     * @param authorizers the authorizers
     * @return a forbidden error
     */
    protected void forbidden(final J2EContext context, final List<CommonProfile> profiles, final String authorizers) {
        HttpAction.forbidden(context);
    }

    /**
     * Return whether we must start a login process if the first client is an indirect one.
     *
     * @param context        the web context
     * @param currentClients the current clients
     * @return whether we must start a login process
     */
    protected boolean startAuthentication(final J2EContext context, final List<Client> currentClients) {
        return isNotEmpty(currentClients) && currentClients.get(0) instanceof IndirectClient;
    }

    /**
     * Save the requested url.
     *
     * @param context        the web context
     * @param currentClients the current clients
     */
    protected void saveRequestedUrl(final J2EContext context, final List<Client> currentClients) {
        if (ajaxRequestResolver == null || !ajaxRequestResolver.isAjax(context)) {
            final String requestedUrl = context.getFullRequestURL();
            log.debug("requestedUrl: {}", requestedUrl);
            context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, requestedUrl);
        }
    }

    /**
     * Perform a redirection to start the login process of the first indirect client.
     *
     * @param context        the web context
     * @param currentClients the current clients
     * @return the performed redirection
     */
    protected void redirectToIdentityProvider(final J2EContext context, final List<Client> currentClients) {
        final IndirectClient currentClient = (IndirectClient) currentClients.get(0);
        currentClient.redirect(context);
    }

    /**
     * Return an unauthorized error.
     *
     * @param context        the web context
     * @param currentClients the current clients
     * @return an unauthorized error
     */
    protected void unauthorized(final J2EContext context, final List<Client> currentClients) {
        HttpAction.unauthorized(context);
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "clientFinder", this.clientFinder, "authorizationChecker", this.authorizationChecker,
                "profileStorageDecision", this.profileStorageDecision, "ajaxRequestResolver", this.ajaxRequestResolver);
    }
}
