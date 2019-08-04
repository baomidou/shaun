package com.baomidou.shaun.core.filter;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;

import java.util.List;
import java.util.Optional;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import com.baomidou.shaun.core.context.GlobalConfig;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.mgt.SecurityManager;

import lombok.Data;
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
public class CallbackFilter implements ShaunFilter {

    private ClientFinder clientFinder = new DefaultCallbackClientFinder();
    private OnlyPathMatcher pathMatcher;
    private Clients clients;
    private SecurityManager securityManager;
    private String indexUrl;
    private CallbackHandler callbackHandler;

    @Override
    public boolean goOnChain(JEEContext context) {
        if (pathMatcher.matches(context)) {
            log.debug("=== CALLBACK ===");

            List<Client> foundClients = clientFinder.find(this.clients, context, null);
            assertTrue(foundClients != null && foundClients.size() == 1,
                    "unable to find one indirect client for the callback: check the callback URL for a client name parameter");
            final Client foundClient = foundClients.get(0);
            log.debug("foundClient: {}", foundClient);
            assertNotNull("foundClient", foundClient);

            final Optional<Credentials> credentials = foundClient.getCredentials(context);
            log.debug("credentials: {}", credentials);

            if (credentials.isPresent()) {
                final Optional<UserProfile> profile = foundClient.getUserProfile(credentials.get(), context);
                log.debug("profile: {}", profile);

                if (profile.isPresent()) {
                    CommonProfile commonProfile = callbackHandler.callBack(context, profile.get());
                    securityManager.login(commonProfile);
                    GlobalConfig.gotoUrl(context, indexUrl);
                    return false;
                }
            }
            if (GlobalConfig.getAjaxRequestResolver().isAjax(context)) {
                throw UnauthorizedAction.INSTANCE;
            }
            GlobalConfig.gotoLoginUrl(context);
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
        CommonHelper.assertNotBlank("indexUrl", indexUrl);
        CommonHelper.assertNotNull("clients", clients);
        CommonHelper.assertNotNull("securityManager", securityManager);
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
    }
}
