package com.baomidou.mipac4j.core.filter;

import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;
import lombok.Data;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.util.CommonHelper;

/**
 * 回调 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class DefaultCallbackFilter implements Pac4jFilter {

    private final Config config;
    private final Matcher matcher;
    private CallbackLogic<Boolean, J2EContext> callbackLogic = new DefaultCallbackLogic<>();

    public DefaultCallbackFilter(final String callbackUrl, final Config config) {
        this.config = config;
        if (CommonHelper.isNotBlank(callbackUrl)) {
            this.matcher = new OnlyPathMatcher(callbackUrl);
        } else {
            this.matcher = OnlyPathMatcher.NO_MATCHER;
        }
    }

    @Override
    public boolean goOnChain(J2EContext context) {
        return false;
    }
}
