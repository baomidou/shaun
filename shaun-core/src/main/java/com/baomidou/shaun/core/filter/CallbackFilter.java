package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.engine.CallbackExecutor;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.Optional;

/**
 * 回调 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class CallbackFilter implements ShaunFilter {

    private final CallbackLogic<Boolean, J2EContext> callbackLogic = new DefaultCallbackLogic<>();
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Matcher matcher;

    private Config config;
    private String indexUrl;
    private String callbackUrl;
    private CallbackExecutor callbackExecutor;

    @SuppressWarnings("unchecked")
    @Override
    public boolean goOnChain(J2EContext context) {
        if (matcher.matches(context)) {
            callbackLogic.perform(context, config, (code, ctx) -> false, indexUrl, false,
                    false, false, null);
            Optional<CommonProfile> profile = config.getProfileManagerFactory().apply(context).get(false);
            callbackExecutor.callBack(context, profile.get());
            return false;
        }
        return true;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
        CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
        CommonHelper.assertNotBlank("indexUrl", indexUrl);
        CommonHelper.assertNotNull("callbackExecutor", callbackExecutor);
        CommonHelper.assertNotNull("config", config);
        this.matcher = new OnlyPathMatcher(callbackUrl);
    }
}
