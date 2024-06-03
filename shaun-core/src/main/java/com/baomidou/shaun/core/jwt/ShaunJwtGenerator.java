/*
 * Copyright 2019-2024 baomidou (wonderming@vip.qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.core.jwt;

import com.baomidou.shaun.core.config.ProfileConstants;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Getter;
import lombok.val;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;

import java.util.Date;

/**
 * @author miemie
 * @since 2020-12-07
 */
@Getter
public class ShaunJwtGenerator extends JwtGenerator {

    private Date expirationTime;

    public ShaunJwtGenerator() {
    }

    public ShaunJwtGenerator(SignatureConfiguration signatureConfiguration) {
        super(signatureConfiguration);
    }

    public ShaunJwtGenerator(SignatureConfiguration signatureConfiguration, EncryptionConfiguration encryptionConfiguration) {
        super(signatureConfiguration, encryptionConfiguration);
    }

    @Override
    protected JWTClaimsSet buildJwtClaimsSet(UserProfile profile) {
        TokenProfile tokenProfile = (TokenProfile) profile;
        // claims builder with subject and issue time
        final Date issueAt = new Date();
        val builder = new JWTClaimsSet.Builder()
                .issueTime(new Date());

        // add attributes
        tokenProfile.getAttributes().forEach(builder::claim);
        builder.claim(INTERNAL_ROLES, tokenProfile.getRoles());
        builder.claim(ProfileConstants.INTERNAL_PERMISSIONS, tokenProfile.getPermissions());
        builder.claim(INTERNAL_LINKEDID, tokenProfile.getLinkedId());

        builder.subject(profile.getId());
        if (expirationTime != null) {
            builder.expirationTime(expirationTime);
            profile.addAttribute(JwtClaims.EXPIRATION_TIME, expirationTime);
        }
        profile.addAttribute(JwtClaims.ISSUED_AT, issueAt);
        // claims
        return builder.build();
    }

    @Override
    public void setExpirationTime(final Date expirationTime) {
        this.expirationTime = new Date(expirationTime.getTime());
    }
}
