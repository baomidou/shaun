package com.baomidou.mipac4j.autoconfigure.factory;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;

import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.filter.LogoutFilter;
import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author miemie
 * @since 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LogoutFilterFactoryBean extends AbstractPac4jFilterFactoryBean<LogoutFilter> {

    private MIPac4jProperties properties;
    private LogoutExecutor logoutExecutor;
    private ProfileManagerFactory profileManagerFactory;
    private boolean willBeUse;
    private SessionStore sessionStore;
    private Client client;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Config config;

    @Override
    protected LogoutFilter createInstance() {
        LogoutFilter filter = new LogoutFilter();
        filter.setWillBeUse(willBeUse);
        filter.setLogoutExecutor(logoutExecutor);
        filter.setLogoutUrl(properties.getLogoutUrl());
        filter.setOutThenUrl(properties.getIndexUrl());
        filter.setConfig(config);
        return filter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CommonHelper.isNotBlank(properties.getLogoutUrl())) {
            // check
            CommonHelper.assertNotNull("client", client);
            CommonHelper.assertNotNull("sessionStore", sessionStore);
            CommonHelper.assertNotNull("logoutExecutor", logoutExecutor);
            CommonHelper.assertNotNull("profileManagerFactory", profileManagerFactory);

            willBeUse = true;
            config = new Config();
            Clients clients = new Clients();
            clients.setClients(client);
            clients.setDefaultSecurityClients(client.getName());
            config.setClients(clients);
            config.setSessionStore(sessionStore);
            config.setProfileManagerFactory(profileManagerFactory);
        }
    }
}
