package com.baomidou.mipac4j.autoconfigure.factory;

import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.filter.SecurityFilter;
import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;
import lombok.AccessLevel;
import lombok.Setter;
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

import java.util.Map;

/**
 * @author miemie
 * @since 2019-07-24
 */
public class SecurityFilterFactoryBean extends AbstractPac4jFilterFactoryBean<SecurityFilter> {

    private final Matcher matcher;
    private final Client client;
    private final SessionStore sessionStore;
    private final ListableBeanFactory beanFactory;
    private final MIPac4jProperties properties;
    private final ProfileManagerFactory profileManagerFactory;
    @Setter(AccessLevel.NONE)
    private Config config;
    @Setter(AccessLevel.NONE)
    private String authorizers;

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
    protected SecurityFilter createInstance() {
        SecurityFilter filter = new SecurityFilter();
        filter.setConfig(config);
        filter.setAuthorizers(authorizers);
        filter.setMarchers(Pac4jConstants.MATCHERS);
        return filter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        config = new Config();
        Map<String, Authorizer> authorizeMap = beanFactory.getBeansOfType(Authorizer.class);
        String au = properties.getAuthorizers();
        if (!CollectionUtils.isEmpty(authorizeMap)) {
            config.setAuthorizers(authorizeMap);
            String s = String.join(Pac4jConstants.ELEMENT_SEPRATOR, authorizeMap.keySet());
            if (StringUtils.hasText(au)) {
                au += (Pac4jConstants.ELEMENT_SEPRATOR + s);
            } else {
                au = s;
            }
        }
        this.authorizers = au;
        Clients clients = new Clients();
        clients.setClients(client);
        clients.setDefaultSecurityClients(client.getName());
        config.setClients(clients);
        config.setSessionStore(sessionStore);
        config.setProfileManagerFactory(profileManagerFactory);
        config.addMatcher(Pac4jConstants.MATCHERS, matcher);
    }
}
