/*
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
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
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
@SuppressWarnings("unchecked")
@Slf4j
@Data
@RequiredArgsConstructor
public class SfLoginFilter implements ShaunFilter {

    private final Matcher pathMatcher;
    private Clients clients;
    private ClientFinder clientFinder = new DefaultSfClientFinder();

    @Override
    public HttpAction doFilter(CoreConfig config, JEEContext context) {
        if (pathMatcher.matches(context)) {
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
        return null;
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("clients", clients);
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
    }
}
