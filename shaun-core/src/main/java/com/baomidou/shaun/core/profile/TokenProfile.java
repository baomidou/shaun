package com.baomidou.shaun.core.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.core.util.CommonHelper;

import java.util.Date;

/**
 * @author miemie
 * @since 2019-12-27
 */
public class TokenProfile extends CommonProfile {

    private String token;

    public String getToken() {
        return token;
    }

    public TokenProfile setToken(final String token) {
        CommonHelper.assertNull("TokenProfile.token", this.token);
        CommonHelper.assertNotBlank("TokenProfile.token", token);
        this.token = token;
        return this;
    }

    public Date getIssuedAt() {
        return (Date) getAttribute(JwtClaims.ISSUED_AT);
    }

    public Date getExpirationDate() {
        return (Date) getAttribute(JwtClaims.EXPIRATION_TIME);
    }
}
