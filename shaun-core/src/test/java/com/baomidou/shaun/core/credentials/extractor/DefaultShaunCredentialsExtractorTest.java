package com.baomidou.shaun.core.credentials.extractor;

import com.baomidou.shaun.core.BaseTokenTest;
import com.baomidou.shaun.core.mgt.DefaultProfileTokenManager;
import com.baomidou.shaun.core.profile.TokenProfile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-12-03
 */
@Slf4j
class DefaultShaunCredentialsExtractorTest extends BaseTokenTest {

    @Test
    void xx() {
        DefaultProfileTokenManager manager = new DefaultProfileTokenManager(signatureConfiguration, encryptionConfiguration, null);
        final JwtAuthenticator authenticator = new JwtAuthenticator(signatureConfiguration, encryptionConfiguration);
        for (int i = 0; i < 100; i++) {
            String token = manager.generateToken(profile(i), "1d");
            if (token.length() > 4000) {
                log.info("添加 permission 到第 {} 个时,token 长度超过了 4000", i);
                log.info("换算一下每个uuid长32,id长32,总长: {}", 32 * (i + 1));
                break;
            }
            TokenCredentials credentials = new TokenCredentials(token);
            TokenProfile profile = (TokenProfile) authenticator.validateToken(credentials.getToken());
            assertThat(profile).isNotNull();
        }
    }

    TokenProfile profile(int permissionSize) {
        TokenProfile profile = new TokenProfile(uuid());
        for (int i = 0; i < permissionSize; i++) {
            profile.addPermission(uuid());
        }
        return profile;
    }
}