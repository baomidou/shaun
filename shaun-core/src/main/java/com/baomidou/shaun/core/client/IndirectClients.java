package com.baomidou.shaun.core.client;

import lombok.Getter;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author miemie
 * @since 2019-07-24
 */
@Getter
public class IndirectClients {

    private final List<Client> clients = new ArrayList<>();

    /**
     * 把 IndirectClients 获取到的 Profile 依据指定的转换器转换后,通过 TokenGenerator 把 Profile 变为 token
     *
     * @param client IndirectClient
     * @return this
     */
    public IndirectClients addClient(IndirectClient client) {
        clients.add(client);
        return this;
    }
}
