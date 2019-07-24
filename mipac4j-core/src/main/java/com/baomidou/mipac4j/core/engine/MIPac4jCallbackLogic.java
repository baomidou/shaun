package com.baomidou.mipac4j.core.engine;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;
import static org.pac4j.core.util.CommonHelper.toNiceString;

import java.util.List;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.engine.AbstractExceptionAwareLogic;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

import com.baomidou.mipac4j.core.adapter.CommonProfileAdapter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * todo 待改造
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("unchecked")
public class MIPac4jCallbackLogic<R, C extends WebContext, P extends CommonProfile> extends AbstractExceptionAwareLogic<R, C> implements CallbackLogic<R, C, P> {

    private ClientFinder clientFinder = new DefaultCallbackClientFinder();

    @Override
    public R perform(C context, Config config, HttpActionAdapter<R, C> httpActionAdapter, String indexUrl, CommonProfileAdapter<P, CommonProfile> commonProfileAdapter) {
        logger.debug("=== CALLBACK ===");

        HttpAction action;
        try {
            // checks
            assertNotNull("clientFinder", clientFinder);
            assertNotNull("context", context);
            assertNotNull("config", config);
            assertNotNull("httpActionAdapter", httpActionAdapter);
            assertNotBlank("indexUrl", indexUrl);
            final Clients clients = config.getClients();
            assertNotNull("clients", clients);

            // logic
            final List<Client> foundClients = clientFinder.find(clients, context, null);
            assertTrue(foundClients != null && foundClients.size() == 1,
                    "unable to find one indirect client for the callback: check the callback URL for a client name parameter or suffix path"
                            + " or ensure that your configuration defaults to one indirect client");
            final Client foundClient = foundClients.get(0);
            logger.debug("foundClient: {}", foundClient);
            assertNotNull("foundClient", foundClient);

            final Credentials credentials = foundClient.getCredentials(context);
            logger.debug("credentials: {}", credentials);

            final CommonProfile profile = foundClient.getUserProfile(credentials, context);
            logger.debug("profile: {}", profile);
            saveUserProfile(context, config, profile);
            action = redirectToIndexUrl(context, indexUrl);
        } catch (final RuntimeException e) {
            return handleException(e, httpActionAdapter, context);
        }

        return httpActionAdapter.adapt(action.getCode(), context);
    }

    protected void saveUserProfile(final C context, final Config config, final CommonProfile profile) {
        final ProfileManager manager = getProfileManager(context, config);
        if (profile != null) {
            manager.save(false, profile, false);
            final SessionStore<C> sessionStore = context.getSessionStore();
            sessionStore.destroySession(context);
        }
    }

    protected HttpAction redirectToIndexUrl(final C context, final String redirectUrl) {
        logger.debug("redirectUrl: {}", redirectUrl);
        return HttpAction.redirect(context, redirectUrl);
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "clientFinder", clientFinder, "errorUrl", getErrorUrl());
    }
}
