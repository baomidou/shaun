/*
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
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
import org.pac4j.core.profile.jwt.AbstractJwtProfile;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.springframework.util.Assert;

import java.util.List;

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
     * 设置 iss
     */
    public void setIssuer(String iss) {
        addAttribute(JwtClaims.ISSUER, iss);
    }

    /**
     * 设置 aud
     */
    public void setAudience(List<String> aud) {
        addAttribute(JwtClaims.AUDIENCE, aud);
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
}
