package com.baomidou.mipac4j.core.filter;

import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.List;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;

/**
 * 登出 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Slf4j
public class DefaultLogoutFilter implements Pac4jFilter {

    private final Matcher logoutMatcher;
    private final AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
    private final LogoutExecutor logoutExecutor;
    private final String outThenUrl;
    private final ProfileManagerFactory profileManagerFactory;

    public DefaultLogoutFilter(final String logoutUrl, final String outThenUrl, final LogoutExecutor logoutExecutor,
                               final ProfileManagerFactory profileManagerFactory) {
        this.logoutExecutor = logoutExecutor;
        this.outThenUrl = outThenUrl;
        this.profileManagerFactory = profileManagerFactory;
        assertNotBlank("logoutUrl", logoutUrl);
        this.logoutMatcher = ctx -> ctx.getPath().equals(logoutUrl);
    }

    @Override
    public int order() {
        return 200;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean goOnChain(J2EContext context) {
        if (logoutMatcher.matches(context)) {
            log.debug("into logoutUrl");
            List<CommonProfile> profiles = profileManagerFactory.apply(context).getAll(false);
            logoutExecutor.logout(context, profiles);
            if (CommonHelper.isNotBlank(outThenUrl) && !ajaxRequestResolver.isAjax(context)) {
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, outThenUrl);
                context.setResponseStatus(HttpConstants.TEMP_REDIRECT);
                log.debug("logout success, then redirect to [{}]", outThenUrl);
            }
            return false;
        }
        return true;
    }
}
