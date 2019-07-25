package com.baomidou.mipac4j.core.filter;

import com.baomidou.mipac4j.core.engine.SecurityLogic;
import lombok.Data;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.matching.MatchingChecker;
import org.pac4j.core.matching.RequireAllMatchersChecker;

/**
 * 安全 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class DefaultSecurityFilter implements Pac4jFilter {

    private MatchingChecker matchingChecker = new RequireAllMatchersChecker();

    private SecurityLogic securityLogic = new SecurityLogic();

    private Config config;

    private String authorizers;

    private String matchers;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (matchingChecker.matches(context, matchers, config.getMatchers())) {
            return securityLogic.perform(context, config, config.getClients().getDefaultSecurityClients(),
                    authorizers, matchers, false);
        }
        return true;
    }

    @Override
    public int order() {
        return 100;
    }
}
