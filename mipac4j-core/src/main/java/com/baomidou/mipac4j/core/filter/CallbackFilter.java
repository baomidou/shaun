package com.baomidou.mipac4j.core.filter;

import java.util.Optional;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 回调 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class CallbackFilter implements Pac4jFilter {

    private final CallbackLogic<Boolean, J2EContext> callbackLogic = new DefaultCallbackLogic<>();
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Matcher matcher;
    private Config config;

    private String indexUrl;
    private String callbackUrl;
    private SessionStore sessionStore;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (matcher.matches(context)) {
            callbackLogic.perform(context, config, (code, ctx) -> false, indexUrl, false,
                    false, false, null);
            Optional<CommonProfile> optional = config.getProfileManagerFactory().apply(context).get(false);

        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
        this.matcher = new OnlyPathMatcher(callbackUrl);
    }
}
