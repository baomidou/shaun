package com.baomidou.mipac4j.autoconfigure.factory;

import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.filter.LogoutFilter;
import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;

/**
 * @author miemie
 * @since 2019-07-24
 */
public class LogoutFilterFactoryBean extends AbstractPac4jFilterFactoryBean<LogoutFilter> {

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
    protected LogoutFilter createInstance() {
        return new LogoutFilter(properties.getLogoutUrl(), properties.getLoginUrl(), logoutExecutor,
                profileManagerFactory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
