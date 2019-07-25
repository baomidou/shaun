package com.baomidou.mipac4j.autoconfigure.factory;

import java.util.Map;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.util.CommonHelper;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.baomidou.mipac4j.core.context.http.DoHttpAction;
import com.baomidou.mipac4j.core.filter.SecurityFilter;
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
public class SecurityFilterFactoryBean extends AbstractPac4jFilterFactoryBean<SecurityFilter> {

    private Matcher matcher;
    private Client client;
    private SessionStore sessionStore;
    private ListableBeanFactory beanFactory;
    private ProfileManagerFactory profileManagerFactory;
    private DoHttpAction doHttpAction;
    private String authorizers;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Config config;

    @Override
    protected SecurityFilter createInstance() {
        SecurityFilter filter = new SecurityFilter();
        filter.setConfig(config);
        filter.setAuthorizers(authorizers);
        filter.setMarchers(Pac4jConstants.MATCHERS);
        filter.setDoHttpAction(doHttpAction);
        return filter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // check
        CommonHelper.assertNotNull("matcher", matcher);
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("sessionStore", sessionStore);
        CommonHelper.assertNotNull("profileManagerFactory", profileManagerFactory);

        config = new Config();
        Map<String, Authorizer> authorizeMap = beanFactory.getBeansOfType(Authorizer.class);
        String au = authorizers;
        if (!CollectionUtils.isEmpty(authorizeMap)) {
            config.setAuthorizers(authorizeMap);
            String s = String.join(Pac4jConstants.ELEMENT_SEPRATOR, authorizeMap.keySet());
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
        config.setClients(clients);
        config.setSessionStore(sessionStore);
        config.setProfileManagerFactory(profileManagerFactory);
        config.addMatcher(Pac4jConstants.MATCHERS, matcher);
    }
}
