package com.baomidou.mipac4j.autoconfigure.factory;

import java.util.ArrayList;
import java.util.List;

import org.pac4j.core.context.session.SessionStore;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;

import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.filter.MIPac4jFilter;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author miemie
 * @since 2019-07-22
 */
@Data
@Slf4j
public class MIPac4jFilterFactoryBean implements FactoryBean<MIPac4jFilter>, InitializingBean {

    private final SessionStore sessionStore;
    private final ListableBeanFactory beanFactory;
    private MIPac4jFilter instance;
    private J2EContextFactory j2EContextFactory;
    private List<Pac4jFilter> pac4jFilters;

    public MIPac4jFilterFactoryBean(ListableBeanFactory beanFactory, J2EContextFactory j2EContextFactory,
                                    SessionStore sessionStore) {
        this.beanFactory = beanFactory;
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
        pac4jFilters = new ArrayList<>(beanFactory.getBeansOfType(Pac4jFilter.class).values());
    }
}
