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
package com.baomidou.shaun.core.profile;

import com.baomidou.shaun.core.config.ProfileConstants;
import lombok.Setter;
import org.pac4j.core.profile.AttributeLocation;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

import java.util.List;
import java.util.Map;

/**
 * copy from {@link org.pac4j.jwt.profile.JwtProfileDefinition}
 *
 * @author miemie
 * @since 2024-06-01
 */
@Setter
public class TokenProfileDefinition extends CommonProfileDefinition {
    private boolean keepNestedAttributes = true;

    public TokenProfileDefinition() {
        super(x -> new TokenProfile());
        setRestoreProfileFromTypedId(true);
        ProfileHelper.setProfileClassPrefixes(List.of("org.pac4j.", "com.baomidou.shaun."));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void convertAndAdd(UserProfile profile, AttributeLocation attributeLocation, String name, Object value) {
        if (value instanceof Map) {
            var jsonObject = (Map<String, ?>) value;
            jsonObject.forEach((key, objectValue) -> super.convertAndAdd(profile, attributeLocation, key, objectValue));
            if (keepNestedAttributes) {
                super.convertAndAdd(profile, attributeLocation, name, value);
            }
        } else {
            if (ProfileConstants.INTERNAL_PERMISSIONS.equals(name)) {
                ((TokenProfile) profile).addPermissions((List<String>) value);
            } else {
                super.convertAndAdd(profile, attributeLocation, name, value);
            }
        }
    }
}
