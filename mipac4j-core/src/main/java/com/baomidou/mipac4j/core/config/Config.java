package com.baomidou.mipac4j.core.config;

import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.util.CommonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author miemie
 * @since 2019-07-25
 */
@Data
@NoArgsConstructor
public class Config {

    protected Matcher matcher;
    protected Clients clients;
    protected Map<String, Authorizer> authorizeMap = new HashMap<>();
    protected SessionStore sessionStore;
    protected ProfileManagerFactory profileManagerFactory;
    private String authorizes;

    public Config(final Client client) {
        this.clients = new Clients(client);
    }

    public Config(final Clients clients) {
        this.clients = clients;
    }

    public Config(final List<Client> clients) {
        this.clients = new Clients(clients);
    }

    public Config(final Client... clients) {
        this.clients = new Clients(clients);
    }

    public Config(final String callbackUrl, final Client client) {
        this.clients = new Clients(callbackUrl, client);
    }

    public Config(final String callbackUrl, final Client... clients) {
        this.clients = new Clients(callbackUrl, clients);
    }

    public Config(final String callbackUrl, final List<Client> clients) {
        this.clients = new Clients(callbackUrl, clients);
    }

    public Clients getClients() {
        return clients;
    }

    public void setAuthorizeMap(final Map<String, Authorizer> authorizeMap) {
        CommonHelper.assertNotNull("authorizers", authorizeMap);
        this.authorizeMap = authorizeMap;
    }

    public void setAuthorizer(final Authorizer authorizer) {
        CommonHelper.assertNotNull("authorizer", authorizer);
        this.authorizeMap.put(authorizer.getClass().getSimpleName(), authorizer);
    }

    public void addAuthorizer(final String name, final Authorizer authorizer) {
        authorizeMap.put(name, authorizer);
    }
}
