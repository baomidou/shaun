package com.baomidou.mipac4j.autoconfigure.filter;

import java.util.Map;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
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

import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.filter.MIPac4jFilter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author miemie
 * @since 2019-07-22
 */
@Data
@Slf4j
public class MIPac4jFilterFactoryBean implements FactoryBean<MIPac4jFilter>, InitializingBean {

    private final Matcher matcher;
    private final Client client;
    private final SessionStore sessionStore;
    private final LogoutExecutor logoutExecutor;
    private final ListableBeanFactory beanFactory;
    private MIPac4jFilter instance;
    private MIPac4jProperties properties;
    private J2EContextFactory j2EContextFactory;
    private Config securityConfig;
    private String authorizers;

    public MIPac4jFilterFactoryBean(MIPac4jProperties properties, ListableBeanFactory beanFactory, Matcher matcher,
                                    J2EContextFactory j2EContextFactory, Client client,
                                    SessionStore sessionStore, LogoutExecutor logoutExecutor) {
        this.properties = properties;
        this.beanFactory = beanFactory;
        this.j2EContextFactory = j2EContextFactory;
        this.matcher = matcher;
        this.client = client;
        this.sessionStore = sessionStore;
        this.logoutExecutor = logoutExecutor;
    }

    @Override
    public MIPac4jFilter getObject() throws Exception {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }

    private MIPac4jFilter createInstance() {
        MIPac4jFilter filter = new MIPac4jFilter();
        filter.setSecurityConfig(securityConfig);
        filter.setAuthorizers(authorizers);
        filter.setMatchers(Pac4jConstants.MATCHERS);
        filter.setJ2EContextFactory(j2EContextFactory);
        filter.setLogoutUrl(properties.getLogoutUrl());
        filter.setLogoutExecutor(logoutExecutor);
        return filter;
    }

    @Override
    public Class<?> getObjectType() {
        return MIPac4jFilter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
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
        securityConfig.setHttpActionAdapter((code, context) -> false);
        securityConfig.setProfileManagerFactory(ProfileManager::new);
        securityConfig.addMatcher(Pac4jConstants.MATCHERS, matcher);
    }
}