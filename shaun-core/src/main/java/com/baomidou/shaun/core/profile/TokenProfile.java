/*
 * Copyright 2019-2022 baomidou (wonderming@vip.qq.com)
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
package com.baomidou.shaun.core.profile;

import com.baomidou.shaun.core.config.ProfileConstants;
import lombok.NoArgsConstructor;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.jwt.AbstractJwtProfile;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.core.util.Pac4jConstants;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.List;
import java.util.Locale;

/**
 * @author miemie
 * @since 2019-12-27
 */
@NoArgsConstructor
public class TokenProfile extends AbstractJwtProfile {
    private static final long serialVersionUID = -1;

    private String token;

    public TokenProfile(String id) {
        this.setId(id);
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        Assert.isNull(this.token, "token just can set once");
        Assert.hasText(token, "token cannot be black");
        this.token = token;
    }

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    public String getTenantId() {
        return (String) getAttribute(ProfileConstants.tenantId);
    }

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public void setTenantId(String tenantId) {
        addAttribute(ProfileConstants.tenantId, tenantId);
    }

    /* 以下是对父类的 get method 的对应 set method 的补充 */

    /**
     * {@link #getIssuer()}
     */
    public void setIssuer(String iss) {
        addAttribute(JwtClaims.ISSUER, iss);
    }

    /**
     * {@link #getAudience()}
     */
    public void setAudience(List<String> aud) {
        addAttribute(JwtClaims.AUDIENCE, aud);
    }

    /**
     * {@link #getEmail()}
     */
    public void setEmail(String email) {
        addAttribute(CommonProfileDefinition.EMAIL, email);
    }

    /**
     * {@link #getFirstName()}
     */
    public void setFirstName(String firstName) {
        addAttribute(CommonProfileDefinition.FIRST_NAME, firstName);
    }

    /**
     * {@link #getFamilyName()}
     */
    public void setFamilyName(String familyName) {
        addAttribute(CommonProfileDefinition.FAMILY_NAME, familyName);
    }

    /**
     * {@link #getDisplayName()}
     */
    public void setDisplayName(String displayName) {
        addAttribute(CommonProfileDefinition.DISPLAY_NAME, displayName);
    }

    /**
     * {@link #getUsername()}
     */
    public void setUsername(String username) {
        addAttribute(Pac4jConstants.USERNAME, username);
    }

    @Override
    public Gender getGender() {
        String attribute = getAttribute(CommonProfileDefinition.GENDER, String.class);
        if (attribute == null) {
            return Gender.UNSPECIFIED;
        }
        return Gender.valueOf(attribute);
    }

    /**
     * {@link #getGender()}
     */
    public void setGender(Gender gender) {
        addAttribute(CommonProfileDefinition.GENDER, gender.name());
    }

    @Override
    public Locale getLocale() {
        String attribute = getAttribute(CommonProfileDefinition.LOCALE, String.class);
        return attribute == null ? null : Locale.forLanguageTag(attribute);
    }

    /**
     * {@link #getLocale()}
     */
    public void setLocale(Locale locale) {
        addAttribute(CommonProfileDefinition.LOCALE, locale.toLanguageTag());
    }

    @Override
    public URI getPictureUrl() {
        String attribute = getAttribute(CommonProfileDefinition.PICTURE_URL, String.class);
        return attribute == null ? null : URI.create(attribute);
    }

    /**
     * {@link #getPictureUrl()}
     */
    public void setPictureUrl(URI pictureUrl) {
        addAttribute(CommonProfileDefinition.PICTURE_URL, pictureUrl.toASCIIString());
    }

    @Override
    public URI getProfileUrl() {
        String attribute = getAttribute(CommonProfileDefinition.PROFILE_URL, String.class);
        return attribute == null ? null : URI.create(attribute);
    }

    /**
     * {@link #getProfileUrl()}
     */
    public void setProfileUrl(URI profileUrl) {
        addAttribute(CommonProfileDefinition.PROFILE_URL, profileUrl.toASCIIString());
    }

    /**
     * {@link #getLocation()}
     */
    public void setLocation(String location) {
        addAttribute(CommonProfileDefinition.LOCATION, location);
    }
}
