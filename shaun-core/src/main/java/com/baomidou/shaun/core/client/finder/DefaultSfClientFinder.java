/**
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.core.client.finder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.Pac4jConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author miemie
 * @since 2019-08-04
 */
@Slf4j
@Data
public class DefaultSfClientFinder implements ClientFinder {

    private String clientNameParameter = Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;

    @Override
    public List<Client<? extends Credentials>> find(final Clients clients, final WebContext context, final String clientNames) {
        final List<Client<? extends Credentials>> result = new ArrayList<>();
        final Optional<String> clientNameOnRequest = context.getRequestParameter(clientNameParameter);
        log.debug("clientNameOnRequest: {}", clientNameOnRequest);
        if (clientNameOnRequest.isPresent()) {
            // from the request
            final Optional<Client> client = clients.findClient(clientNameOnRequest.get());
            client.ifPresent(result::add);
        }
        return result;
    }
}
