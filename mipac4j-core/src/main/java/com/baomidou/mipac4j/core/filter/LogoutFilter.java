package com.baomidou.mipac4j.core.filter;

import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;
import com.baomidou.mipac4j.core.profile.ProfileManagerFactory;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
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
@Data
@EqualsAndHashCode(callSuper = true)
public class LogoutFilter extends AbstractPac4jFilter {

    private LogoutExecutor logoutExecutor;
    private String outThenUrl;
    private ProfileManagerFactory profileManagerFactory;
    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
    @Setter(AccessLevel.NONE)
    private Matcher matcher;

    private String logoutUrl;

    public LogoutFilter(final String logoutUrl, final String outThenUrl, final LogoutExecutor logoutExecutor,
                        final ProfileManagerFactory profileManagerFactory) {
        this.logoutExecutor = logoutExecutor;
        this.outThenUrl = outThenUrl;
        this.profileManagerFactory = profileManagerFactory;
        assertNotBlank("logoutUrl", logoutUrl);
        this.matcher = ctx -> ctx.getPath().equals(logoutUrl);
    }

    @Override
    public boolean filterChain(J2EContext context) {
        if (matcher.matches(context)) {
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

    @Override
    protected void initIfNeed() {
        CommonHelper.assertNotBlank("callbackUrl", logoutUrl);
        this.matcher = new OnlyPathMatcher(logoutUrl);
    }

    @Override
    public int order() {
        return 300;
    }
}
