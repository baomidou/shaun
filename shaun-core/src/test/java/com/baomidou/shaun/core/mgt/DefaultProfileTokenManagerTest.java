package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.profile.TokenProfile;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-12-02
 */
class DefaultProfileTokenManagerTest {

    private final static String SIGN = "9bfd771777984dc89868fd258f823e48";

    @Test
    void token() {
        SecretSignatureConfiguration signatureConfiguration = new SecretSignatureConfiguration(SIGN);
        SecretEncryptionConfiguration encryptionConfiguration = new SecretEncryptionConfiguration(SIGN);
        DefaultProfileTokenManager manager = new DefaultProfileTokenManager(signatureConfiguration,
                encryptionConfiguration, null);
        TokenProfile profile = new TokenProfile();
        profile.setId("11111");
        profile.setLinkedId("22222");
        profile.setTenantId("222222222");
        profile.addPermission("permission");
        profile.addRole("role");
        profile.setIssuer("iss");
        profile.setAudience(Lists.newArrayList("iss"));
        profile.addAuthenticationAttribute("2222222", "22222222");
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