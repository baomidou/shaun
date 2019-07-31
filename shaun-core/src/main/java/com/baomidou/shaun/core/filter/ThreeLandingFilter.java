package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.engine.DefaultSecurityLogic;
import com.baomidou.shaun.core.engine.SecurityLogic;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import lombok.Data;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.util.CommonHelper;

/**
 * 三方登陆 filter
 * 主要是 oauth 和 cas
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class ThreeLandingFilter implements ShaunFilter {

    private final SecurityLogic securityLogic = new DefaultSecurityLogic();
    private OnlyPathMatcher pathMatcher;
    private Config config;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (pathMatcher.matches(context)) {
            return securityLogic.perform(context, config, config.getClients().getDefaultSecurityClients(),
                    null);
        }
        return true;
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        CommonHelper.assertNotNull("config", config);
    }
}
