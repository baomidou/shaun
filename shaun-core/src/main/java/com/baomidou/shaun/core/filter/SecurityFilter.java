package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.context.http.DoHttpAction;
import com.baomidou.shaun.core.engine.DefaultSecurityLogic;
import com.baomidou.shaun.core.engine.SecurityLogic;
import lombok.Data;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.util.CommonHelper;

/**
 * 安全 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class SecurityFilter implements ShaunFilter {

    private final SecurityLogic securityLogic = new DefaultSecurityLogic();
    private Config config;
    private String marchers;
    private String authorizers;
    private DoHttpAction doHttpAction;

    @Override
    public boolean goOnChain(J2EContext context) {
        return securityLogic.perform(context, config, config.getClients().getDefaultSecurityClients(), authorizers, marchers);
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
