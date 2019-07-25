package com.baomidou.mipac4j.core.filter;

import com.baomidou.mipac4j.core.config.Config;
import com.baomidou.mipac4j.core.engine.SecurityLogic;
import lombok.Data;
import org.pac4j.core.context.J2EContext;

/**
 * 安全 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class DefaultSecurityFilter implements Pac4jFilter {

    private SecurityLogic securityLogic = new SecurityLogic();

    private Config config;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (config.getMatcher().matches(context)) {
            return securityLogic.perform(context, config, config.getClients().getDefaultSecurityClients());
        }
        return true;
    }

    @Override
    public int order() {
        return 100;
    }
}
