package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.BaseTokenTest;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-12-02
 */
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
}