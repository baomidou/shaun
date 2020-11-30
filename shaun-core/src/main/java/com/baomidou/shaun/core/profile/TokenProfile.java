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
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author miemie
 * @since 2019-12-27
 */
public class TokenProfile extends CommonProfile {
    private static final long serialVersionUID = -1;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        Assert.isNull(this.token, "token just can set once");
        Assert.hasText(token, "token cannot be black");
        this.token = token;
    }

    public Date getIssuedAt() {
        return (Date) getAttribute(JwtClaims.ISSUED_AT);
    }

    public Date getExpirationDate() {
        return (Date) getAttribute(JwtClaims.EXPIRATION_TIME);
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
