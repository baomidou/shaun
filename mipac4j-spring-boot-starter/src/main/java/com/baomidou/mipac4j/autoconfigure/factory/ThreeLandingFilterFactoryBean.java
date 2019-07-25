package com.baomidou.mipac4j.autoconfigure.factory;

import com.baomidou.mipac4j.autoconfigure.properties.MIPac4jProperties;
import com.baomidou.mipac4j.core.client.IndirectClients;
import com.baomidou.mipac4j.core.filter.ThreeLandingFilter;
import lombok.AllArgsConstructor;
import org.pac4j.core.config.Config;

/**
 * @author miemie
 * @since 2019-07-25
 */
@AllArgsConstructor
public class ThreeLandingFilterFactoryBean extends AbstractPac4jFilterFactoryBean<ThreeLandingFilter> {

    private final MIPac4jProperties pac4jProperties;
    private final IndirectClients indirectClients;
    private Config config;

    @Override
    protected ThreeLandingFilter createInstance() {
        return new ThreeLandingFilter(pac4jProperties.getThreeLandingUrl(), config);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        config = new Config(pac4jProperties.getCallbackUrl(), indirectClients.getClients());
    }
}
