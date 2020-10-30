package com.baomidou.shaun.core.spring.boot;

import com.baomidou.shaun.core.authority.AuthorityManager;
import com.baomidou.shaun.core.authority.DefaultAuthorityManager;
import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.context.Cookie;
import com.baomidou.shaun.core.context.Header;
import com.baomidou.shaun.core.context.Parameter;
import com.baomidou.shaun.core.enums.TokenLocation;
import com.baomidou.shaun.core.extractor.TokenExtractor;
import com.baomidou.shaun.core.mgt.DefaultProfileManager;
import com.baomidou.shaun.core.mgt.ProfileManager;
import com.baomidou.shaun.core.mgt.SecurityManager;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;

/**
 * @author miemie
 * @since 2020-10-30
 */
public class ShaunBeanConfigurationSupport {

    public SignatureConfiguration getSignatureConfiguration(String salt) {
        return new SecretSignatureConfiguration(salt);
    }

    public EncryptionConfiguration getEncryptionConfiguration(String salt) {
        return new SecretEncryptionConfiguration(salt);
    }

    public CredentialsExtractor<TokenCredentials> getCredentialsExtractor(TokenLocation tokenLocation, Header header,
                                                                          Cookie cookie, Parameter parameter) {
        return new TokenExtractor(tokenLocation, header, cookie, parameter);
    }

    public ProfileManager getProfileManager(SignatureConfiguration signatureConfiguration,
                                            EncryptionConfiguration encryptionConfiguration,
                                            CredentialsExtractor<TokenCredentials> credentialsExtractor) {
        return new DefaultProfileManager(signatureConfiguration, encryptionConfiguration, credentialsExtractor);
    }

    public AuthorityManager getAuthorityManager(String skipAuthenticationRolePermission) {
        return new DefaultAuthorityManager(skipAuthenticationRolePermission);
    }

    public SecurityManager getSecurityManager(Config config, TokenLocation tokenLocation) {
        return new SecurityManager(config, tokenLocation);
    }
}
