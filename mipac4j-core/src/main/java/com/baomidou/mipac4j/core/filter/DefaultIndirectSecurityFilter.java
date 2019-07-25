package com.baomidou.mipac4j.core.filter;

import lombok.Data;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;

/**
 * 安全 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class DefaultIndirectSecurityFilter implements Pac4jFilter {

    private SecurityLogic<Boolean, J2EContext> securityLogic = new DefaultSecurityLogic<>();

    private Config config;

    private String authorizers;

    private String matchers;

    @Override
    public boolean goOnChain(J2EContext context) {
        return securityLogic.perform(context, config, (ctx, pf, param) -> true, (code, ctx) -> false,
                config.getClients().getDefaultSecurityClients(), authorizers, matchers, false);
    }

    @Override
    public int order() {
        return 100;
    }
}
