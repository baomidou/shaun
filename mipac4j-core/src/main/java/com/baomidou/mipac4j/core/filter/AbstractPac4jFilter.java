package com.baomidou.mipac4j.core.filter;

import lombok.Data;
import org.pac4j.core.context.J2EContext;

/**
 * @author miemie
 * @since 2019-07-25
 */
@Data
public abstract class AbstractPac4jFilter implements Pac4jFilter {

    /**
     * 这个 Filter 是否将被使用
     */
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
