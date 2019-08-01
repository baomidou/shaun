package com.baomidou.shaun.core.engine;

import com.baomidou.shaun.core.handler.CallbackHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

import java.util.List;
import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * @author miemie
 * @since 2019-07-31
 */
@SuppressWarnings("unchecked")
@Slf4j
@Data
public class DefaultCallbackLogic implements CallbackLogic {

    private ClientFinder clientFinder = new DefaultCallbackClientFinder();

    @Override
    public boolean perform(final JEEContext context, final Config config, final String defaultUrl,
                           final CallbackHandler callbackExecutor) {
        log.debug("=== CALLBACK ===");

        HttpAction action;

        // checks
        assertNotNull("clientFinder", clientFinder);
        assertNotNull("context", context);
        assertNotNull("config", config);
        assertNotBlank(Pac4jConstants.DEFAULT_URL, defaultUrl);
        final Clients clients = config.getClients();
        assertNotNull("clients", clients);

        // logic
        final List<Client> foundClients = clientFinder.find(clients, context, null);
        assertTrue(foundClients != null && foundClients.size() == 1,
                "unable to find one indirect client for the callback: check the callback URL for a client name parameter or suffix path"
                        + " or ensure that your configuration defaults to one indirect client");
        final Client foundClient = foundClients.get(0);
        log.debug("foundClient: {}", foundClient);
        assertNotNull("foundClient", foundClient);

        final Optional<Credentials> credentials = foundClient.getCredentials(context);
        log.debug("credentials: {}", credentials);
        if (credentials.isPresent()) {
            final Optional<CommonProfile> sourceProfile = foundClient.getUserProfile(credentials.get(), context);
        }

        log.debug("sourceProfile: {}", sourceProfile);
        final CommonProfile profile = callbackExecutor.callBack(context, sourceProfile);
        log.debug("profile: {}", profile);
        saveUserProfile(context, config, profile, true, false, true);
        action = HttpAction.redirect(context, defaultUrl);

        if (action.getCode() == HttpConstants.TEMP_REDIRECT) {
            return false;
        }
        throw action;
    }

    protected void saveUserProfile(final JEEContext context, final Config config, final CommonProfile profile,
                                   final boolean saveInSession, final boolean multiProfile, final boolean renewSession) {
        final ProfileManager manager = config.getProfileManagerFactory().apply(context);
        if (profile != null) {
            manager.save(saveInSession, profile, multiProfile);
            if (renewSession) {
                renewSession(context, config);
            }
        }
    }

    protected void renewSession(final JEEContext context, final Config config) {
        final SessionStore<JEEContext> sessionStore = context.getSessionStore();
        if (sessionStore != null) {
            final String oldSessionId = sessionStore.getOrCreateSessionId(context);
            final boolean renewed = sessionStore.renewSession(context);
            if (renewed) {
                final String newSessionId = sessionStore.getOrCreateSessionId(context);
                log.debug("Renewing session: {} -> {}", oldSessionId, newSessionId);
                final Clients clients = config.getClients();
                if (clients != null) {
                    final List<Client> clientList = clients.getClients();
                    for (final Client client : clientList) {
                        final BaseClient baseClient = (BaseClient) client;
                        baseClient.notifySessionRenewal(oldSessionId, context);
                    }
                }
            } else {
                log.error("Unable to renew the session. The session store may not support this feature");
            }
        } else {
            log.error("No session store available for this web context");
        }
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "clientFinder", clientFinder);
    }
}
