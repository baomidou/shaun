package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.BaseTokenTest;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-12-02
 */
@Slf4j
class DefaultProfileTokenManagerTest extends BaseTokenTest {

    @Test
    void token() {
        DefaultProfileTokenManager manager = new DefaultProfileTokenManager(signatureConfiguration, encryptionConfiguration, null);
        TokenProfile profile = new TokenProfile();
        profile.setId(uuid());
        profile.setLinkedId(uuid());
        profile.setTenantId(uuid());
        profile.addPermission(uuid());
        profile.addRole(uuid());
        profile.setIssuer(uuid());
        profile.setAudience(Lists.newArrayList(uuid()));
        profile.addAuthenticationAttribute(uuid(), uuid());
        assertThat(profile.getAuthenticationAttributes()).isNotEmpty();
        System.out.println(profile);
        System.out.println("----------------------------------------------------------------------------------");
        String token = manager.generateToken(profile, "1h");
        System.out.println(token);
        System.out.println(token.length());
        JwtAuthenticator authenticator = new JwtAuthenticator(signatureConfiguration, encryptionConfiguration);
        profile = (TokenProfile) authenticator.validateToken(token);
        System.out.println(profile);
        assertThat(profile).isNotNull();
        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getLinkedId()).isNotNull();
        assertThat(profile.getTenantId()).isNotNull();
        assertThat(profile.getPermissions().size()).isEqualTo(1);
        assertThat(profile.getRoles().size()).isEqualTo(1);
        assertThat(profile.getIssuedAt()).isNotNull();
        assertThat(profile.getExpirationDate()).isNotNull();
        assertThat(profile.getIssuer()).isNotNull();
        assertThat(profile.getAudience()).isNotNull();
        assertThat(profile.getAudience().size()).isEqualTo(1);
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Attributes: " + profile.getAttributes());
        System.out.println("ExpirationDate: " + profile.getExpirationDate());
        System.out.println("IssuedAt: " + profile.getIssuedAt());
        System.out.println("Issuer: " + profile.getIssuer());
        System.out.println("Audience: " + profile.getAudience());
        System.out.println("NotBefore: " + profile.getNotBefore());
        System.out.println("AuthenticationAttributes: " + profile.getAuthenticationAttributes());
        assertThat(profile.getAuthenticationAttributes()).isEmpty();
    }

    @Test
    void lengthInfo() {
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