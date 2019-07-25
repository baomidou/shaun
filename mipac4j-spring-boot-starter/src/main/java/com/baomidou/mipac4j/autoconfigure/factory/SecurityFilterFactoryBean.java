package com.baomidou.mipac4j.autoconfigure.factory;

import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.config.Config;
import com.baomidou.mipac4j.core.filter.DefaultSecurityFilter;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;
import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.Matcher;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

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
    private Config config;

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
        filter.setConfig(config);
        return filter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        config = new Config();
        Map<String, Authorizer> authorizeMap = beanFactory.getBeansOfType(Authorizer.class);
        String au = properties.getAuthorizers();
        if (!CollectionUtils.isEmpty(authorizeMap)) {
            config.setAuthorizeMap(authorizeMap);
            String s = String.join(Pac4jConstants.ELEMENT_SEPRATOR, authorizeMap.keySet());
            if (StringUtils.hasText(au)) {
                au += (Pac4jConstants.ELEMENT_SEPRATOR + s);
            } else {
                au = s;
            }
        }
        config.setAuthorizes(au);
        Clients clients = new Clients();
        clients.setClients(client);
        clients.setDefaultSecurityClients(client.getName());
        config.setClients(clients);
        config.setSessionStore(sessionStore);
        config.setProfileManagerFactory(profileManagerFactory);
        config.setMatcher(matcher);
    }
}
