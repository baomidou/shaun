package com.baomidou.mipac4j.core.filter;

import org.pac4j.core.context.J2EContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * @author miemie
 * @since 2019-07-25
 */
public abstract class AbstractPac4jFilter implements Pac4jFilter {

    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 这个 Filter 是否将被使用
     */
    @Setter
    @Getter
    private boolean willBeUse;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (isWillBeUse()) {
            return filterChain(context);
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (isWillBeUse()) {
            initMustNeed();
        }
    }

    public abstract boolean filterChain(J2EContext context);

    protected abstract void initMustNeed();
}
