package com.baomidou.mipac4j.core.filter;

import com.baomidou.mipac4j.core.client.TokenClient;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.matching.OnlyPathMatcher;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * 登出 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class LogoutFilter implements Pac4jFilter {

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Matcher matcher;

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
    private LogoutExecutor logoutExecutor;
    private String logoutUrl;
    private TokenClient tokenDirectClient;

    @SuppressWarnings("unchecked")
    @Override
    public boolean goOnChain(J2EContext context) {
        if (matcher.matches(context)) {
            final TokenCredentials credentials = tokenDirectClient.getCredentials(context);
            final CommonProfile profile = tokenDirectClient.getUserProfile(credentials, context);
            logoutExecutor.logout(context, profile);
            return false;
        }
        return true;
    }

    @Override
    public int order() {
        return 300;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotBlank("logoutUrl", logoutUrl);
        CommonHelper.assertNotNull("logoutExecutor", logoutExecutor);
        this.matcher = new OnlyPathMatcher(logoutUrl);
    }
}
