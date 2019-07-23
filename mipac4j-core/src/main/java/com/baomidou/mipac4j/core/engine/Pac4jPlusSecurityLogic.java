package com.baomidou.mipac4j.core.engine;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.isEmpty;
import static org.pac4j.core.util.CommonHelper.isNotEmpty;
import static org.pac4j.core.util.CommonHelper.toNiceString;

import java.util.Collections;
import java.util.List;

import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultSecurityClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.engine.AbstractExceptionAwareLogic;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.matching.MatchingChecker;
import org.pac4j.core.matching.RequireAllMatchersChecker;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * copy from {@link DefaultSecurityLogic} ,删减一些不必要的东西
 *
 * @author miemie
 * @since 2019-07-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Pac4jPlusSecurityLogic<R, C extends WebContext> extends AbstractExceptionAwareLogic<R, C> implements SecurityLogic<R, C> {

    private ClientFinder clientFinder = new DefaultSecurityClientFinder();

    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();

    private MatchingChecker matchingChecker = new RequireAllMatchersChecker();

    @SuppressWarnings("unchecked")
    @Override
    public R perform(final C context, final Config config, final SecurityGrantedAccessAdapter<R, C> securityGrantedAccessAdapter,
                     final HttpActionAdapter<R, C> httpActionAdapter,
                     final String clients, final String authorizers, final String matchers, final Boolean inputMultiProfile,
                     final Object... parameters) {

        logger.debug("=== SECURITY ===");

        HttpAction action;
        try {

            // default value
            final boolean multiProfile = inputMultiProfile == null ? false : inputMultiProfile;

            // checks
            assertNotNull("context", context);
            assertNotNull("config", config);
            assertNotNull("httpActionAdapter", httpActionAdapter);
            assertNotNull("clientFinder", clientFinder);
            assertNotNull("authorizationChecker", authorizationChecker);
            assertNotNull("matchingChecker", matchingChecker);
            final Clients configClients = config.getClients();
            assertNotNull("configClients", configClients);

            // logic
            logger.debug("url: {}", context.getFullRequestURL());
            logger.debug("matchers: {}", matchers);
            if (matchingChecker.matches(context, matchers, config.getMatchers())) {

                logger.debug("clients: {}", clients);
                final List<Client> currentClients = clientFinder.find(configClients, context, clients);
                logger.debug("currentClients: {}", currentClients);

                final ProfileManager manager = getProfileManager(context, config);
                List<CommonProfile> profiles = manager.getAll(false);
                logger.debug("profiles: {}", profiles);

                // no profile and some current clients
                if (isEmpty(profiles) && isNotEmpty(currentClients)) {
                    boolean updated = false;
                    // loop on all clients searching direct ones to perform authentication
                    for (final Client currentClient : currentClients) {
                        if (currentClient instanceof DirectClient) {
                            logger.debug("Performing authentication for direct client: {}", currentClient);

                            final Credentials credentials = currentClient.getCredentials(context);
                            logger.debug("credentials: {}", credentials);
                            final CommonProfile profile = currentClient.getUserProfile(credentials, context);
                            logger.debug("profile: {}", profile);
                            if (profile != null) {
                                logger.debug("multiProfile: {}", multiProfile);
                                manager.save(false, profile, multiProfile);
                                updated = true;
                                if (!multiProfile) {
                                    break;
                                }
                            }
                        }
                    }
                    if (updated) {
                        profiles = manager.getAll(false);
                        logger.debug("new profiles: {}", profiles);
                    }
                }

                // we have profile(s) -> check authorizations
                if (isNotEmpty(profiles)) {
                    logger.debug("authorizers: {}", authorizers);
                    if (authorizationChecker.isAuthorized(context, profiles, authorizers, config.getAuthorizers())) {
                        logger.debug("authenticated and authorized -> grant access");
                        return securityGrantedAccessAdapter.adapt(context, profiles, parameters);
                    } else {
                        logger.debug("forbidden");
                        action = forbidden(context);
                    }
                } else {
                    logger.debug("unauthorized");
                    action = unauthorized(context);
                }

            } else {

                logger.debug("no matching for this request -> grant access");
                return securityGrantedAccessAdapter.adapt(context, Collections.emptyList(), parameters);
            }

        } catch (final Exception e) {
            return handleException(e, httpActionAdapter, context);
        }

        return httpActionAdapter.adapt(action.getCode(), context);
    }

    /**
     * Return a forbidden error.
     *
     * @param context the web context
     * @return a forbidden error
     */
    protected HttpAction forbidden(final C context) {
        return HttpAction.forbidden(context);
    }

    /**
     * Return an unauthorized error.
     *
     * @param context the web context
     * @return an unauthorized error
     */
    protected HttpAction unauthorized(final C context) {
        return HttpAction.unauthorized(context);
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "clientFinder", this.clientFinder, "authorizationChecker", this.authorizationChecker,
                "matchingChecker", this.matchingChecker);
    }
}
