package com.baomidou.mipac4j.core.adapter;

import com.baomidou.mipac4j.core.client.IndirectClients;
import com.baomidou.mipac4j.core.converter.ProfileConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-24
 */
@Data
@AllArgsConstructor
public class DefaultHttpActionAdapter<R extends CommonProfile, P extends CommonProfile> implements HttpActionAdapter<R, P> {

    private final IndirectClients indirectClients;

    @Override
    public R adapt(P profile, IndirectClient client) {
        ProfileConverter converter = indirectClients.findConverterByClient(client);
        // todo 根据流程变化,待定
        if (converter != null) {
            return (R) converter.converter(profile);
        }
        return (R) profile;
    }
}
