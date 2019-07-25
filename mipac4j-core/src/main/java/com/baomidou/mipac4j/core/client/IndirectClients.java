package com.baomidou.mipac4j.core.client;

import com.baomidou.mipac4j.core.converter.ProfileConverter;
import lombok.AccessLevel;
import lombok.Getter;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author miemie
 * @since 2019-07-24
 */
@Getter
public class IndirectClients {

    @Getter(AccessLevel.NONE)
    private final Map<String, ProfileConverter> profileConverterMap = new HashMap<>();

    private final List<Client> clients = new ArrayList<>();

    /**
     * 不需要进行转换,直接通过 TokenGenerator 把 Profile 变为 token
     *
     * @param client IndirectClient
     * @return this
     */
    public IndirectClients addClient(IndirectClient client) {
        return addClient(client, ProfileConverter.NO_THING);
    }

    /**
     * 把 IndirectClients 获取到的 Profile 依据指定的转换器转换后,通过 TokenGenerator 把 Profile 变为 token
     *
     * @param client IndirectClient
     * @return this
     */
    public IndirectClients addClient(IndirectClient client, ProfileConverter converter) {
        clients.add(client);
        profileConverterMap.put(client.getName(), converter);
        return this;
    }

    public ProfileConverter findConverterByClient(Client client) {
        if (client instanceof IndirectClient) {
            return profileConverterMap.getOrDefault(client.getName(), ProfileConverter.NO_THING);
        }
        return null;
    }
}
