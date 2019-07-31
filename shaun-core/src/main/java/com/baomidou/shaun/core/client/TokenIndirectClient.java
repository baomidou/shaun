package com.baomidou.shaun.core.client;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.redirect.RedirectAction;

/**
 * @author miemie
 * @since 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TokenIndirectClient extends IndirectClient<TokenCredentials, CommonProfile> {

    public TokenIndirectClient(final String loginUrl, final CredentialsExtractor<TokenCredentials> credentialsExtractor,
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
