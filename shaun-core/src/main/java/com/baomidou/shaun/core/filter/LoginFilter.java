package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.handler.login.LoginHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.profile.ProfileManagerFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * 登出 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@SuppressWarnings("all")
@Slf4j
@Data
public class LoginFilter implements ShaunFilter {

    private OnlyPathMatcher pathMatcher;
    private CredentialsExtractor credentialsExtractor;
    private IndirectClient client;
    private String indexUrl;
    private LoginHandler loginHandler;
    private ProfileManagerFactory profileManagerFactory;

    @Override
    public boolean goOnChain(J2EContext context) {
        if (pathMatcher.matches(context)) {
            Credentials credentials = credentialsExtractor.extract(context);
            CommonProfile profile = loginHandler.login(context, credentials);
            if (profile == null) {
                client.redirect(context); // 不抛出这个异常
                return false;
            }
            ProfileManager manager = profileManagerFactory.apply(context);
            manager.save(true, profile, false);
            redirectToOriginallyRequestedUrl(context, indexUrl); // 不抛出这个异常
            return false;
        }
        return true;
    }

    private HttpAction redirectToOriginallyRequestedUrl(final J2EContext context, final String defaultUrl) {
        final String requestedUrl = (String) context.getSessionStore().get(context, Pac4jConstants.REQUESTED_URL);
        String redirectUrl = defaultUrl;
        if (isNotBlank(requestedUrl)) {
            context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, null);
            redirectUrl = requestedUrl;
        }
        log.debug("redirectUrl: {}", redirectUrl);
        return HttpAction.redirect(context, redirectUrl);
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("credentialsExtractor", credentialsExtractor);
        CommonHelper.assertNotNull("loginHandler", loginHandler);
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        CommonHelper.assertNotNull("profileManagerFactory", profileManagerFactory);
    }
}
