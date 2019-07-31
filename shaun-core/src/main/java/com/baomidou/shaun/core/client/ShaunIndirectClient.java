package com.baomidou.shaun.core.client;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.redirect.RedirectAction;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author miemie
 * @since 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShaunIndirectClient extends IndirectClient<TokenCredentials, CommonProfile> {

    public ShaunIndirectClient(final String loginUrl, final CredentialsExtractor<TokenCredentials> credentialsExtractor,
                               final Authenticator<TokenCredentials> tokenAuthenticator) {
        defaultCredentialsExtractor(credentialsExtractor);
        defaultAuthenticator(tokenAuthenticator);
        setCallbackUrl(loginUrl);
        defaultRedirectActionBuilder(ctx -> {
            final String finalLoginUrl = getUrlResolver().compute(loginUrl, ctx);
            return RedirectAction.redirect(finalLoginUrl);
        });
    }

    @Override
    protected void clientInit() {
        // ignore
    }
}
