package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.handler.login.LoginHandler;
import com.baomidou.shaun.core.matching.OnlyPathMatcher;
import com.baomidou.shaun.core.profile.ProfileManagerFactory;
import lombok.Data;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;

/**
 * 登出 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Data
public class LoginFilter implements ShaunFilter {

    private OnlyPathMatcher pathMatcher;
    private CredentialsExtractor credentialsExtractor;
    private IndirectClient client;
    private String indexUrl;
    private LoginHandler loginHandler;
    private ProfileManagerFactory profileManagerFactory;

    @SuppressWarnings("unchecked")
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
            HttpAction.redirect(context, indexUrl); // 不抛出这个异常
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
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("credentialsExtractor", credentialsExtractor);
        CommonHelper.assertNotNull("loginHandler", loginHandler);
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        CommonHelper.assertNotNull("profileManagerFactory", profileManagerFactory);
    }
}
