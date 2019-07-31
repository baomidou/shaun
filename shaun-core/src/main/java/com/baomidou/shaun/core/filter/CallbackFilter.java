package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.engine.CallbackLogic;
import com.baomidou.shaun.core.engine.DefaultCallbackLogic;
import com.baomidou.shaun.core.handler.CallbackHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import lombok.Data;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.util.CommonHelper;

/**
 * 回调 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class CallbackFilter implements ShaunFilter {

    private final CallbackLogic callbackLogic = new DefaultCallbackLogic();
    private OnlyPathMatcher pathMatcher;
    private Config config;
    private String indexUrl;
    private CallbackHandler callbackHandler;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (pathMatcher.matches(context)) {
            return callbackLogic.perform(context, config, indexUrl, callbackHandler);
        }
        return true;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotBlank("indexUrl", indexUrl);
        CommonHelper.assertNotNull("callbackExecutor", callbackHandler);
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        CommonHelper.assertNotNull("config", config);
    }
}
