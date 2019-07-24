package com.baomidou.mipac4j.autoconfigure.factory;

import java.util.Map;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.Matcher;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.context.ProfileManagerFactory;
import com.baomidou.mipac4j.core.filter.DefaultSecurityFilter;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;

/**
 * @author miemie
 * @since 2019-07-24
 */
public class SecurityFilterFactoryBean extends AbstractPac4jFilterFactoryBean {

    private final Matcher matcher;
    private final Client client;
    private final SessionStore sessionStore;
    private final ListableBeanFactory beanFactory;
    private final MIPac4jProperties properties;
    private final ProfileManagerFactory profileManagerFactory;
    private String authorizers;
    private Config securityConfig;

    public SecurityFilterFactoryBean(final MIPac4jProperties properties, final ListableBeanFactory beanFactory,
                                     final Matcher matcher, final Client client, final SessionStore sessionStore,
                                     final ProfileManagerFactory profileManagerFactory) {
        this.properties = properties;
        this.beanFactory = beanFactory;
        this.matcher = matcher;
        this.client = client;
        this.sessionStore = sessionStore;
        this.profileManagerFactory = profileManagerFactory;
    }

    @Override
    protected Pac4jFilter createInstance() {
        DefaultSecurityFilter filter = new DefaultSecurityFilter();
        filter.setAuthorizers(authorizers);
        filter.setConfig(securityConfig);
        filter.setMatchers(Pac4jConstants.MATCHERS);
        return filter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        securityConfig = new Config();
        Map<String, Authorizer> authorizerMap = beanFactory.getBeansOfType(Authorizer.class);
        String au = properties.getAuthorizers();
        if (!CollectionUtils.isEmpty(authorizerMap)) {
            securityConfig.setAuthorizers(authorizerMap);
            String s = String.join(Pac4jConstants.ELEMENT_SEPRATOR, authorizerMap.keySet());
            if (StringUtils.hasText(au)) {
                au += (Pac4jConstants.ELEMENT_SEPRATOR + s);
            } else {
                au = s;
            }
        }
        authorizers = au;
        Clients clients = new Clients();
        clients.setClients(client);
        clients.setDefaultSecurityClients(client.getName());
        securityConfig.setClients(clients);
        securityConfig.setSessionStore(sessionStore);
        securityConfig.setProfileManagerFactory(profileManagerFactory);
        securityConfig.addMatcher(Pac4jConstants.MATCHERS, matcher);
    }
}
