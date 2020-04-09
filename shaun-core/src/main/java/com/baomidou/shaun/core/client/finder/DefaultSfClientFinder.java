package com.baomidou.shaun.core.client.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.Pac4jConstants;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author miemie
 * @since 2019-08-04
 */
@Slf4j
@Data
public class DefaultSfClientFinder implements ClientFinder {

    private String clientNameParameter = Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;

    @Override
    public List<Client> find(final Clients clients, final WebContext context, final String clientNames) {
        final List<Client> result = new ArrayList<>();
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
