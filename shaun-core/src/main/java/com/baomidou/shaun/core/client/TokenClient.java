package com.baomidou.shaun.core.client;

import com.baomidou.shaun.core.credentials.extractor.ShaunCredentialsExtractor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;

/**
 * 检索 token 并验证
 *
 * @author miemie
 * @since 2019-07-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TokenClient extends DirectClient<TokenCredentials> {

    public TokenClient(final ShaunCredentialsExtractor credentialsExtractor,
                       final JwtAuthenticator tokenAuthenticator) {
        defaultCredentialsExtractor(credentialsExtractor);
        defaultAuthenticator(tokenAuthenticator);
    }

    @Override
    protected void clientInit() {
        // ignore
    }
}
