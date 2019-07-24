package com.baomidou.mipac4j.stateless.autoconfigure.filter;

import java.util.Map;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.profile.ProfileManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.baomidou.mipac4j.core.client.TokenDirectClient;
import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.filter.Pac4jPlusFilter;
import com.baomidou.mipac4j.stateless.autoconfigure.properties.MiPac4jProperties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author miemie
 * @since 2019-07-22
 */
@Data
@Slf4j
public class Pac4jPlusFilterFactoryBean implements FactoryBean<Pac4jPlusFilter>, InitializingBean {

    private final Matcher matcher;
    private final TokenDirectClient tokenClient;
    private final SessionStore sessionStore;
    private final LogoutExecutor logoutExecutor;
    private final ListableBeanFactory beanFactory;
    private Pac4jPlusFilter instance;
    private MiPac4jProperties properties;
    private J2EContextFactory j2EContextFactory;
    private Config config;
    private String authorizers;

    public Pac4jPlusFilterFactoryBean(MiPac4jProperties properties, ListableBeanFactory beanFactory, Matcher matcher,
                                      J2EContextFactory j2EContextFactory, TokenDirectClient tokenClient,
                                      SessionStore sessionStore, LogoutExecutor logoutExecutor) {
        this.properties = properties;
        this.beanFactory = beanFactory;
        this.j2EContextFactory = j2EContextFactory;
        this.matcher = matcher;
        this.tokenClient = tokenClient;
        this.sessionStore = sessionStore;
        this.logoutExecutor = logoutExecutor;
    }

    @Override
    public Pac4jPlusFilter getObject() throws Exception {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }

    private Pac4jPlusFilter createInstance() {
        Pac4jPlusFilter filter = new Pac4jPlusFilter();
        filter.setConfig(config);
        filter.setAuthorizers(authorizers);
        filter.setMatchers(Pac4jConstants.MATCHERS);
        filter.setJ2EContextFactory(j2EContextFactory);
        filter.setLogoutUrl(properties.getLogoutUrl());
        filter.setLogoutExecutor(logoutExecutor);
        return filter;
    }

    @Override
    public Class<?> getObjectType() {
        return Pac4jPlusFilter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        config = new Config();
        Map<String, Authorizer> authorizerMap = beanFactory.getBeansOfType(Authorizer.class);
        String au = properties.getAuthorizers();
        if (!CollectionUtils.isEmpty(authorizerMap)) {
            config.setAuthorizers(authorizerMap);
            String s = String.join(Pac4jConstants.ELEMENT_SEPRATOR, authorizerMap.keySet());
            if (StringUtils.hasText(au)) {
                au += (Pac4jConstants.ELEMENT_SEPRATOR + s);
            } else {
                au = s;
            }
        }
        authorizers = au;
        Clients clients = new Clients();
        clients.setClients(tokenClient);
        clients.setDefaultSecurityClients(tokenClient.getName());
        config.setClients(clients);
        config.setSessionStore(sessionStore);
        config.setHttpActionAdapter((code, context) -> false);
        config.setProfileManagerFactory(ProfileManager::new);
        config.addMatcher(Pac4jConstants.MATCHERS, matcher);
    }
}
