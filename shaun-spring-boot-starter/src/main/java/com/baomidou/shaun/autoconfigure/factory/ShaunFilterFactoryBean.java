package com.baomidou.shaun.autoconfigure.factory;

import com.baomidou.shaun.core.context.JEEContextFactory;
import com.baomidou.shaun.core.filter.MIPac4jFilter;
import com.baomidou.shaun.core.filter.ShaunFilter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * @author miemie
 * @since 2019-07-22
 */
@Data
@Slf4j
public class ShaunFilterFactoryBean implements FactoryBean<MIPac4jFilter>, InitializingBean {

    private final SessionStore sessionStore;
    private final JEEContextFactory j2EContextFactory;
    private MIPac4jFilter instance;
    private List<ShaunFilter> pac4jFilters;

    public ShaunFilterFactoryBean(JEEContextFactory j2EContextFactory, SessionStore sessionStore) {
        this.j2EContextFactory = j2EContextFactory;
        this.sessionStore = sessionStore;
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
        filter.setFilterList(pac4jFilters);
        filter.setJ2EContextFactory(j2EContextFactory);
        filter.setSessionStore(sessionStore);
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
        CommonHelper.assertNotNull("pac4jFilters", pac4jFilters);
    }
}
