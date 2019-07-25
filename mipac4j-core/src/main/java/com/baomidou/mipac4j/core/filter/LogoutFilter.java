package com.baomidou.mipac4j.core.filter;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;

import java.util.List;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;
import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

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
    private LogoutExecutor logoutExecutor;
    private String outThenUrl;
    private ProfileManagerFactory profileManagerFactory;
    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
    @Setter(AccessLevel.NONE)
    private Matcher matcher;

    private Config config;
    private String logoutUrl;

    public LogoutFilter(final String logoutUrl, final String outThenUrl, final LogoutExecutor logoutExecutor,
                        final ProfileManagerFactory profileManagerFactory) {
        this.logoutExecutor = logoutExecutor;
        this.outThenUrl = outThenUrl;
        this.profileManagerFactory = profileManagerFactory;
        assertNotBlank("logoutUrl", logoutUrl);
        this.matcher = ctx -> ctx.getPath().equals(logoutUrl);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean filterChain(J2EContext context) {
        if (matcher.matches(context)) {
            logoutLogic.perform(context, config, ((code, ctx) -> true), outThenUrl, null, false, false, false);
            List<CommonProfile> profiles = profileManagerFactory.apply(context).getAll(false);
            logoutExecutor.logout(context, profiles);
            return false;
        }
        return true;
    }

    @Override
    protected void initMustNeed() {
        CommonHelper.assertNotBlank("callbackUrl", logoutUrl);
        this.matcher = new OnlyPathMatcher(logoutUrl);
    }

    @Override
    public int order() {
        return 300;
    }
}
