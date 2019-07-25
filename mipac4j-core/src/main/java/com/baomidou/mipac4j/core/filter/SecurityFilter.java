package com.baomidou.mipac4j.core.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;

/**
 * 安全 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityFilter extends AbstractPac4jFilter {

    private String authorizers;
    private SecurityLogic<Boolean, J2EContext> securityLogic = new DefaultSecurityLogic<>();
    private Config config;

    @Override
    public boolean goOnChain(J2EContext context) {
        return securityLogic.perform(context, config, (ctx, pf, param) -> true, (code, ctx) -> false,
                config.getClients().getDefaultSecurityClients(), authorizers, Pac4jConstants.MATCHERS,
                false);
    }

    @Override
    public boolean filterChain(J2EContext context) {
        return false;
    }

    @Override
    protected void initMustNeed() {

    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public boolean isWillBeUse() {
        return true;
    }
}
