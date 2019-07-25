package com.baomidou.mipac4j.core.filter;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.util.CommonHelper;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private String marchers;

    @Override
    public boolean filterChain(J2EContext context) {
        return securityLogic.perform(context, config, (ctx, pf, param) -> true, (code, ctx) -> false,
                config.getClients().getDefaultSecurityClients(), authorizers, marchers, false);
    }

    @Override
    protected void initMustNeed() {
        CommonHelper.assertNotBlank("marchers", marchers);
        CommonHelper.assertNotNull("config", config);
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
