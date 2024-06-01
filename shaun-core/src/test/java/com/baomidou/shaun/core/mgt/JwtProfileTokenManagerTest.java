package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.BaseTokenTest;
import com.baomidou.shaun.core.jwt.JwtTypeSelector;
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
class JwtProfileTokenManagerTest extends BaseTokenTest {

    @Test
    void token() {
        JwtProfileTokenManager manager = new JwtProfileTokenManager(bothSelector, null);
        TokenProfile profile = new TokenProfile();
        profile.setId(uuid());
        profile.setLinkedId(uuid());
        profile.setTenantId(uuid());
        profile.addRole(uuid());
        profile.addRole(uuid());
        profile.addPermission(uuid());
        profile.addPermission(uuid());
        profile.setIssuer(uuid());
        profile.setAudience(Lists.newArrayList(uuid()));
        profile.addAuthenticationAttribute(uuid(), uuid());
        assertThat(profile.getAuthenticationAttributes()).isNotEmpty();
        System.out.println(profile);
        System.out.println("----------------------------------------------------------------------------------");
        String token = manager.generateToken(profile, "1h");
        assertThat(profile.getIssuedAt()).isNotNull();
        assertThat(profile.getExpirationDate()).isNotNull();
        System.out.println(token);
        System.out.println(token.length());
        JwtAuthenticator authenticator = bothSelector.getAuthenticator();
        profile = (TokenProfile) authenticator.validateToken(token);
        System.out.println(profile);
        assertThat(profile).isNotNull();
        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getLinkedId()).isNotNull();
        assertThat(profile.getTenantId()).isNotNull();
        assertThat(profile.getRoles().size()).isEqualTo(2);
        assertThat(profile.getPermissions().size()).isEqualTo(2);
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

    /**
     * 安全
     * 只存一个ID,token 长度: 456
     */
    @Test
    void lengthInfo() {
        lengthInfo(bothSelector, "签名又加密的");
        // 42个
    }

    /**
     * 只签名会暴露 payload, 不安全
     * 只存一个ID,token 长度: 269
     */
    @Test
    void lengthInfo2() {
        lengthInfo(signatureSelector, "只签名的");
        // 61个
    }

    /**
     * 只加密不会暴露 payload, 安全
     * 只存一个ID,token 长度: 285
     */
    @Test
    void lengthInfo3() {
        lengthInfo(encryptionSelector, "只加密的");
        // 60个
    }

    /**
     * 不签名不加密的 , 不安全
     * 只存一个ID,token 长度: 225
     */
    @Test
    void lengthInfo4() {
        lengthInfo(noneSelector, "不签名不加密的");
        // 62个
    }

    void lengthInfo(JwtTypeSelector selector, String node) {
        ProfileTokenManager manager = new JwtProfileTokenManager(selector, null);
        final JwtAuthenticator authenticator = selector.getAuthenticator();
        String jwt = null;
        int len = 0;
        for (int i = 0; i < 100; i++) {
            String token = manager.generateToken(profile(i), "1d");
            if (i == 0) {
                len = token.length();
            }
            if (i == 2) {
                jwt = token;
            }
            if (token.length() > 3072) {
                log.info("{} - 添加 permission 到第 {} 个时,token 长度超过了 4000", node, i);
                log.info("{} - 换算一下每个uuid长32,id长32,总长: {}", node, 32 * (i + 1));
                break;
            }
            TokenCredentials credentials = new TokenCredentials(token);
            TokenProfile profile = (TokenProfile) authenticator.validateToken(credentials.getToken());
            assertThat(profile).isNotNull();
        }
        log.info("jwt: {}", jwt);
        log.info("{} - 只存一个ID,token 长度: {}", node, len);
    }

    TokenProfile profile(int permissionSize) {
        TokenProfile profile = new TokenProfile(uuid());
        for (int i = 0; i < permissionSize; i++) {
            profile.addPermission(uuid());
        }
        return profile;
    }
}