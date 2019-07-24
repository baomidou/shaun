package com.baomidou.mipac4j.core.client;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-24
 */
public class TokenIndirectClient extends IndirectClient<TokenCredentials, CommonProfile> {

    public TokenIndirectClient(final CredentialsExtractor<TokenCredentials> credentialsExtractor, final Authenticator<TokenCredentials> tokenAuthenticator) {
        defaultCredentialsExtractor(credentialsExtractor);
        defaultAuthenticator(tokenAuthenticator);
    }

    @Override
    protected void clientInit() {

    }
}
