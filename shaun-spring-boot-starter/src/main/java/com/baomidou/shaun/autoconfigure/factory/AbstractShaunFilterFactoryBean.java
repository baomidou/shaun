package com.baomidou.shaun.autoconfigure.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.baomidou.shaun.core.filter.ShaunFilter;

/**
 * @author miemie
 * @since 2019-07-24
 */
public abstract class AbstractShaunFilterFactoryBean<T extends ShaunFilter> implements FactoryBean<T>, InitializingBean {

    private T instance;

    @Override
    public T getObject() throws Exception {
        if (instance == null) {
            afterPropertiesSet();
            instance = createInstance();
        }
        return instance;
    }

    protected abstract T createInstance();

    @Override
    public Class<?> getObjectType() {
        return ShaunFilter.class;
    }
}
