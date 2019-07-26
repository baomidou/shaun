package com.baomidou.mipac4j.core.filter;

import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;

import java.util.List;
import java.util.Optional;

/**
 * 登出 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LogoutFilter extends AbstractPac4jFilter {

    private LogoutLogic<Boolean, J2EContext> logoutLogic = new DefaultLogoutLogic<>();
    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
    private LogoutExecutor logoutExecutor = LogoutExecutor.DO_NOTHING;
    private Config config;
    private String logoutUrl;
    private String outThenUrl;
    @Setter(AccessLevel.NONE)
    private Matcher matcher;

    @SuppressWarnings("unchecked")
    @Override
    public boolean filterChain(J2EContext context) {
        if (matcher.matches(context)) {
            logoutLogic.perform(context, config, ((code, ctx) -> true), outThenUrl, null, false, false, false);
            List<CommonProfile> profiles = config.getProfileManagerFactory().apply(context).getAll(false);
            if (CommonHelper.isNotEmpty(profiles)) {
                Optional<CommonProfile> profile = ProfileHelper.flatIntoOneProfile(profiles);
                logoutExecutor.logout(context, profile);
            } else {
                log.error("not find any profiles from logout");
            }
            return false;
        }
        return true;
    }

    @Override
    protected void initMustNeed() {
        CommonHelper.assertNotBlank("callbackUrl", logoutUrl);
        CommonHelper.assertNotNull("config", config);
        CommonHelper.assertNotNull("ajaxRequestResolver", ajaxRequestResolver);
        CommonHelper.assertNotNull("logoutExecutor", logoutExecutor);
        CommonHelper.assertNotNull("logoutLogic", logoutLogic);
        this.matcher = new OnlyPathMatcher(logoutUrl);
    }

    @Override
    public int order() {
        return 300;
    }
}
