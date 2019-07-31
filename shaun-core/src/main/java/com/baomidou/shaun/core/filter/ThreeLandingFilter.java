package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.matching.Matcher;
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

    private final SecurityLogic<Boolean, J2EContext> securityLogic = new DefaultSecurityLogic<>();
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Matcher matcher;

    private Config config;
    private String threeLandingUrl;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (matcher.matches(context)) {
            return securityLogic.perform(context, config, (ctx, pf, param) -> true, (code, ctx) -> false,
                    config.getClients().getDefaultSecurityClients(), null, Pac4jConstants.MATCHERS,
                    false);
        }
        return true;
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotBlank("threeLandingUrl", threeLandingUrl);
        CommonHelper.assertNotNull("config", config);
        this.matcher = new OnlyPathMatcher(threeLandingUrl);
    }
}
