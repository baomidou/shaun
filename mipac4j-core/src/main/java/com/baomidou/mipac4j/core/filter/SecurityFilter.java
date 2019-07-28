package com.baomidou.mipac4j.core.filter;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.util.CommonHelper;

import com.baomidou.mipac4j.core.context.http.DoHttpAction;

import lombok.Data;

/**
 * 安全 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class SecurityFilter implements Pac4jFilter {

    private final SecurityLogic<Boolean, J2EContext> securityLogic = new DefaultSecurityLogic<>();
    private Config config;
    private String marchers;
    private String authorizers;
    private DoHttpAction doHttpAction;

    @Override
    public boolean goOnChain(J2EContext context) {
        return securityLogic.perform(context, config, (ctx, pf, param) -> true, (code, ctx) -> {
                    doHttpAction.adapt(code, ctx);
                    return false;
                },
                config.getClients().getDefaultSecurityClients(), authorizers, marchers, false);
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotBlank("marchers", marchers);
        CommonHelper.assertNotNull("config", config);
        CommonHelper.assertNotNull("doHttpAction", doHttpAction);
    }
}
