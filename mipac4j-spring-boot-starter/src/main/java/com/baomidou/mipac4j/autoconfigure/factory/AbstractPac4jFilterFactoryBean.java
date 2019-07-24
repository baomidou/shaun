package com.baomidou.mipac4j.autoconfigure.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.baomidou.mipac4j.core.filter.Pac4jFilter;

/**
 * @author miemie
 * @since 2019-07-24
 */
public abstract class AbstractPac4jFilterFactoryBean implements FactoryBean<Pac4jFilter>, InitializingBean {

    private Pac4jFilter instance;

    @Override
    public Pac4jFilter getObject() throws Exception {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }

    protected abstract Pac4jFilter createInstance();

    @Override
    public Class<?> getObjectType() {
        return Pac4jFilter.class;
    }
}
