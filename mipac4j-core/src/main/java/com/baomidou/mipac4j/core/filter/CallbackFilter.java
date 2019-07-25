package com.baomidou.mipac4j.core.filter;

import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.util.CommonHelper;

/**
 * 回调 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CallbackFilter extends AbstractPac4jFilter {

    private final CallbackLogic<Boolean, J2EContext> callbackLogic = new DefaultCallbackLogic<>();
    @Setter(AccessLevel.NONE)
    private Matcher matcher;
    @Setter(AccessLevel.NONE)
    private Config config;

    private String indexUrl;
    private String callbackUrl;
    private SessionStore sessionStore;

    @Override
    public boolean filterChain(J2EContext context) {
        if (matcher.matches(context)) {
            return callbackLogic.perform(context, config, (code, ctx) -> false, indexUrl, false,
                    false, false, null);
        }
        return true;
    }

    @Override
    protected void initMustNeed() {
        CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
        this.matcher = new OnlyPathMatcher(callbackUrl);
    }
}
