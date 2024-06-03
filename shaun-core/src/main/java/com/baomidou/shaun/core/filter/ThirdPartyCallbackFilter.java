/*
 * Copyright 2019-2024 baomidou (wonderming@vip.qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.util.HttpActionInstance;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * callback filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Setter
@Slf4j
public class ThirdPartyCallbackFilter extends AbstractShaunFilter {

    private Clients clients;
    private CallbackHandler callbackHandler;
    private ClientFinder clientFinder = new DefaultCallbackClientFinder();

    public ThirdPartyCallbackFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    protected HttpAction matchThen(CoreConfig config, CallContext context) {
        final List<Client> foundClients = clientFinder.find(this.clients, context.webContext(), null);
        Assert.isTrue(foundClients != null && foundClients.size() == 1,
                "unable to find one indirect client for the callback: check the callback URL for a client name parameter");
        final Client foundClient = foundClients.get(0);
        log.debug("foundClient: {}", foundClient);
        Assert.notNull(foundClient, "foundClient cannot be null");
        final Optional<Credentials> credentials = foundClient.getCredentials(context);
        log.debug("credentials: {}", credentials);
        if (credentials.isPresent()) {
            final Optional<UserProfile> profile = foundClient.getUserProfile(context, credentials.get());
            log.debug("profile: {}", profile);
            if (profile.isPresent()) {
                return callbackHandler.callBack(context, profile.get());
            }
        }
        return HttpActionInstance.BAD_REQUEST;
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("clients", clients);
        CommonHelper.assertNotNull("callbackHandler", callbackHandler);
    }
}
