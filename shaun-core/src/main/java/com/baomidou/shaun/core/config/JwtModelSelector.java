package com.baomidou.shaun.core.config;

import com.baomidou.shaun.core.profile.TokenProfile;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;

/**
 * @author miemie
 * @since 2020-12-03
 */
public interface JwtModelSelector {

    /**
     * jwt encode
     *
     * @return JwtGenerator
     */
    JwtGenerator<TokenProfile> getJwtGenerator();

    /**
     * jwt decode
     *
     * @return JwtAuthenticator
     */
    JwtAuthenticator getJwtAuthenticator();
}
