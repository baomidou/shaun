/*
 * Copyright 2019-2022 baomidou (wonderming@vip.qq.com)
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

import com.baomidou.shaun.core.client.finder.DefaultSfClientFinder;
import com.baomidou.shaun.core.config.CoreConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.BadRequestAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;

import java.util.List;
import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * 三方登录 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Slf4j
@Setter
@SuppressWarnings("unchecked")
public class SfLoginFilter extends AbstractShaunFilter {

    private Clients clients;
    private ClientFinder clientFinder = new DefaultSfClientFinder();

    public SfLoginFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    protected HttpAction matchThen(CoreConfig config, JEEContext context) {
        if (log.isDebugEnabled()) {
            log.debug("access sfLogin \"{}\"", context.getFullRequestURL());
        }
        final List<Client<?>> foundClients = clientFinder.find(this.clients, context, null);
        assertTrue(foundClients != null && foundClients.size() == 1,
                "unable to find one indirect client for the sfLogin: check the sfLogin URL for a client name parameter");
        final Client foundClient = foundClients.get(0);
        log.debug("foundClient: {}", foundClient);
        assertNotNull("foundClient", foundClient);

        Optional<RedirectionAction> redirect = foundClient.getRedirectionAction(context);
        if (redirect.isPresent()) {
            return redirect.get();
        }
        return BadRequestAction.INSTANCE;
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("clients", clients);
    }
}
