package com.baomidou.mipac4j.core.client;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-24
 */
public class TokenIndirectClient extends IndirectClient<TokenCredentials, CommonProfile> {

    @Override
    protected void clientInit() {

    }
}
