package com.baomidou.shaun.core.filter;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;

import java.util.List;
import java.util.Optional;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultSecurityClientFinder;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.util.CommonHelper;

import com.baomidou.shaun.core.context.GlobalConfig;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 前后分离的安全 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@SuppressWarnings("unchecked")
@Slf4j
@Data
public class SfLoginFilter implements ShaunFilter {

    private ClientFinder clientFinder = new DefaultSecurityClientFinder();
    private OnlyPathMatcher pathMatcher;
    private Clients clients;

    @Override
    public boolean goOnChain(JEEContext context) {
        if (pathMatcher.matches(context)) {
            log.debug("=== SF LOGIN ===");

            List<Client> foundClients = clientFinder.find(this.clients, context, null);
            assertTrue(foundClients != null && foundClients.size() == 1,
                    "unable to find one indirect client for the sfLogin: check the sfLogin URL for a client name parameter");
            final Client foundClient = foundClients.get(0);
            log.debug("foundClient: {}", foundClient);
            assertNotNull("foundClient", foundClient);

            Optional<RedirectionAction> redirect = foundClient.redirect(context);
            if (redirect.isPresent()) {
                RedirectionAction action = redirect.get();
                if (action instanceof FoundAction) {
                    GlobalConfig.gotoUrl(context, ((FoundAction) action).getLocation());
                }
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
        CommonHelper.assertNotNull("clients", clients);
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
    }
}
