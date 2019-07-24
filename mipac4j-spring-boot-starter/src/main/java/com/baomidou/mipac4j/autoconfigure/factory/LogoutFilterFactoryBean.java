package com.baomidou.mipac4j.autoconfigure.factory;

import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.context.ProfileManagerFactory;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.filter.DefaultLogoutFilter;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;

/**
 * @author miemie
 * @since 2019-07-24
 */
public class LogoutFilterFactoryBean extends AbstractPac4jFilterFactoryBean {

    private final MIPac4jProperties properties;
    private final LogoutExecutor logoutExecutor;
    private final ProfileManagerFactory profileManagerFactory;

    public LogoutFilterFactoryBean(final MIPac4jProperties properties, final LogoutExecutor logoutExecutor,
                                   final ProfileManagerFactory profileManagerFactory) {
        this.properties = properties;
        this.logoutExecutor = logoutExecutor;
        this.profileManagerFactory = profileManagerFactory;
    }

    @Override
    protected Pac4jFilter createInstance() {
        return new DefaultLogoutFilter(properties.getLogoutUrl(), properties.getLoginUrl(), logoutExecutor,
                profileManagerFactory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
