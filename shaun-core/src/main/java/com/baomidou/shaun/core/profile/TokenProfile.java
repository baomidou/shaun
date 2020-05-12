package com.baomidou.shaun.core.profile;

import java.util.Date;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.core.util.CommonHelper;

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
        CommonHelper.assertNull("token", this.token);
        CommonHelper.assertNotBlank("token", token);
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
