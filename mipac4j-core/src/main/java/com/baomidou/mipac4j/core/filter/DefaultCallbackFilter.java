package com.baomidou.mipac4j.core.filter;

import org.pac4j.core.context.J2EContext;

import com.baomidou.mipac4j.core.engine.CallbackLogic;
import com.baomidou.mipac4j.core.engine.MIPac4jCallbackLogic;

/**
 * 回调 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
public class DefaultCallbackFilter implements Pac4jFilter {

    private CallbackLogic<Boolean, J2EContext> callbackLogic = new MIPac4jCallbackLogic<>();

    @Override
    public boolean goOnChain(J2EContext context) {
        return false;
    }
}
