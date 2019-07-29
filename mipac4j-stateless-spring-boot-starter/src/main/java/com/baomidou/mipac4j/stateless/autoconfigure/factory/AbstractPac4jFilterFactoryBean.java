package com.baomidou.mipac4j.stateless.autoconfigure.factory;

import com.baomidou.mipac4j.core.filter.Pac4jFilter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author miemie
 * @since 2019-07-24
 */
public abstract class AbstractPac4jFilterFactoryBean<T extends Pac4jFilter> implements FactoryBean<T>, InitializingBean {

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
        return Pac4jFilter.class;
    }
}
