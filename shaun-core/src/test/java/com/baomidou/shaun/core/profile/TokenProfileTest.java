package com.baomidou.shaun.core.profile;

import com.baomidou.shaun.core.BaseTokenTest;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.Gender;
import org.pac4j.jwt.profile.JwtGenerator;

import java.net.URI;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2021-04-12
 */
class TokenProfileTest extends BaseTokenTest {

    @Test
    void test() {
        JwtGenerator<TokenProfile> generator = noneSelector.getGenerator();
        TokenProfile profile = new TokenProfile();
        profile.setLocale(Locale.CHINA);
        profile.setGender(Gender.MALE);
        profile.setPictureUrl(URI.create("http://www.java2s.com:8080/yourpath/fileName.htm?stove=10&path=32&id=4#harvic"));
        profile.setProfileUrl(URI.create("http://www.java2s.com:8080/yourpath/fileName.htm?stove=10&path=32&id=4#harvie"));
        // --------------------------------------
        String jwt = generator.generate(profile);
        profile = (TokenProfile) noneSelector.getAuthenticator().validateToken(jwt);
        assertThat(Locale.CHINA).isEqualTo(profile.getLocale());
        assertThat(Gender.MALE).isEqualTo(profile.getGender());
        assertThat(URI.create("http://www.java2s.com:8080/yourpath/fileName.htm?stove=10&path=32&id=4#harvic")).isEqualTo(profile.getPictureUrl());
        assertThat(URI.create("http://www.java2s.com:8080/yourpath/fileName.htm?stove=10&path=32&id=4#harvie")).isEqualTo(profile.getProfileUrl());
    }
}