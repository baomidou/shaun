package com.baomidou.shaun.core.client;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;

/**
 * 检索 token 并验证
 *
 * @author miemie
 * @since 2019-07-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TokenClient extends DirectClient<TokenCredentials> {

    public TokenClient(final CredentialsExtractor<TokenCredentials> credentialsExtractor,
                       final Authenticator<TokenCredentials> tokenAuthenticator) {
        defaultCredentialsExtractor(credentialsExtractor);
        defaultAuthenticator(tokenAuthenticator);
    }

    @Override
    protected void clientInit() {
        // ignore
    }
}
