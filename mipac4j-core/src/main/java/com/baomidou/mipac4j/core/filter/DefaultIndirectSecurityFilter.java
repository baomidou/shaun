package com.baomidou.mipac4j.core.filter;

import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;
import lombok.Data;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.util.CommonHelper;

/**
 * 安全 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class DefaultIndirectSecurityFilter implements Pac4jFilter {

    private final Config config;
    private final Matcher matcher;
    private SecurityLogic<Boolean, J2EContext> securityLogic = new DefaultSecurityLogic<>();

    public DefaultIndirectSecurityFilter(final String indirectUrl, final Config config) {
        if (CommonHelper.isNotBlank(indirectUrl)) {
            this.matcher = new OnlyPathMatcher(indirectUrl);
        } else {
            this.matcher = OnlyPathMatcher.NO_MATCHER;
        }
        this.config = config;
    }

    @Override
    public boolean goOnChain(J2EContext context) {
        return securityLogic.perform(context, config, (ctx, pf, param) -> true, (code, ctx) -> false,
                config.getClients().getDefaultSecurityClients(), null, Pac4jConstants.MATCHERS,
                false);
    }

    @Override
    public int order() {
        return 100;
    }
}
