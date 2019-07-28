package com.baomidou.mipac4j.core.filter;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.matching.Matcher;

import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 三方登陆 filter
 * 主要是 oauth 和 cas
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class ThreeLandingFilter implements Pac4jFilter {

    private final SecurityLogic<Boolean, J2EContext> securityLogic = new DefaultSecurityLogic<>();
    @Setter(AccessLevel.NONE)
    private Config config;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Matcher matcher;

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
        this.matcher = new OnlyPathMatcher(threeLandingUrl);
        this.config = new Config();
    }
}
