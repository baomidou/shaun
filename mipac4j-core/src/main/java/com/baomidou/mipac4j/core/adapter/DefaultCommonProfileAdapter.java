package com.baomidou.mipac4j.core.adapter;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;

import com.baomidou.mipac4j.core.client.IndirectClients;
import com.baomidou.mipac4j.core.converter.ProfileConverter;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author miemie
 * @since 2019-07-24
 */
@Data
@AllArgsConstructor
public class DefaultCommonProfileAdapter implements CommonProfileAdapter<CommonProfile, CommonProfile> {

    private final IndirectClients indirectClients;

    @SuppressWarnings("unchecked")
    @Override
    public CommonProfile adapt(CommonProfile profile, IndirectClient client) {
        ProfileConverter converter = indirectClients.findConverterByClient(client);
        // todo 根据流程变化,待定
        if (converter != null) {
            return converter.converter(profile);
        }
        return profile;
    }
}
