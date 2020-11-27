package com.baomidou.shaun.core.filter;

import java.util.List;
import java.util.Optional;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.profile.UserProfile;
import org.springframework.util.Assert;

import com.baomidou.shaun.core.config.ShaunConfig;
import com.baomidou.shaun.core.handler.CallbackHandler;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * callback filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@SuppressWarnings("unchecked")
@Slf4j
@Data
@RequiredArgsConstructor
public class CallbackFilter implements ShaunFilter {

    private final Matcher pathMatcher;
    private ClientFinder clientFinder = new DefaultCallbackClientFinder();
    private Clients clients;
    private CallbackHandler callbackHandler;

    @Override
    public boolean goOnChain(ShaunConfig config, JEEContext context) {
        if (pathMatcher.matches(context)) {
            log.debug("=== CALLBACK ===");

            final List<Client<?>> foundClients = clientFinder.find(this.clients, context, null);
            Assert.isTrue(foundClients != null && foundClients.size() == 1,
                    "unable to find one indirect client for the callback: check the callback URL for a client name parameter");
            final Client foundClient = foundClients.get(0);
            log.debug("foundClient: {}", foundClient);
            Assert.notNull(foundClient, "foundClient cannot be null");
            final Optional<Credentials> credentials = foundClient.getCredentials(context);
            log.debug("credentials: {}", credentials);
            if (credentials.isPresent()) {
                final Optional<UserProfile> profile = foundClient.getUserProfile(credentials.get(), context);
                log.debug("profile: {}", profile);
                if (profile.isPresent()) {
                    callbackHandler.callBack(context, profile.get());
                    return false;
                }
            }
            if (config.getAjaxRequestResolver().isAjax(context)) {
                config.getHttpActionHandler().preHandle(UnauthorizedAction.INSTANCE, context);
                return false;
            }
            config.redirectLoginUrl(context);
            return false;
        }
        return true;
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void initCheck() {
        Assert.notNull(clients, "clients cannot be null");
        Assert.notNull(callbackHandler, "callbackHandler cannot be null");
    }
}
