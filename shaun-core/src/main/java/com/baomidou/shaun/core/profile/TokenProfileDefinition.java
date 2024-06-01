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
