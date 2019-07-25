package com.baomidou.mipac4j.core.adapter;

import com.baomidou.mipac4j.core.client.IndirectClients;
import com.baomidou.mipac4j.core.converter.ProfileConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-24
 */
@Data
@AllArgsConstructor
public class DefaultCommonProfileAdapter implements CommonProfileAdapter {

    private final IndirectClients indirectClients;

    @SuppressWarnings("unchecked")
    @Override
    public CommonProfile adapt(CommonProfile profile, Client client) {
        ProfileConverter converter = indirectClients.findConverterByClient(client);
        if (converter != null) {
            return converter.converter(profile);
        }
        return profile;
    }
}
