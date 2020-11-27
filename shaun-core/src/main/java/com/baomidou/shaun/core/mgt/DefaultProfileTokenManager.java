package com.baomidou.shaun.core.mgt;

import com.baomidou.shaun.core.client.TokenClient;
import com.baomidou.shaun.core.credentials.extractor.ShaunCredentialsExtractor;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.ExpireTimeUtil;
import lombok.Data;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;

import java.util.Optional;
import java.util.Set;

/**
 * @author miemie
 * @since 2020-10-29
 */
@Data
public class DefaultProfileTokenManager implements ProfileTokenManager {

    private final TokenClient tokenClient;
    private final SignatureConfiguration signatureConfiguration;
    private final EncryptionConfiguration encryptionConfiguration;

    public DefaultProfileTokenManager(SignatureConfiguration signatureConfiguration,
                                      EncryptionConfiguration encryptionConfiguration,
                                      ShaunCredentialsExtractor credentialsExtractor) {
        this.signatureConfiguration = signatureConfiguration;
        this.encryptionConfiguration = encryptionConfiguration;
        this.tokenClient = new TokenClient(credentialsExtractor, new JwtAuthenticator(signatureConfiguration, encryptionConfiguration));
    }

    @Override
    public TokenProfile getProfile(JEEContext context) {
        TokenCredentials credentials = tokenClient.getCredentials(context).orElse(null);
        if (credentials == null) {
            return null;
        }
        Optional<UserProfile> profile = tokenClient.getUserProfile(credentials, context);
        if (profile.isPresent()) {
            // todo 兼容性升级
            CommonProfile commonProfile = (CommonProfile) profile.get();
            TokenProfile tokenProfile;
            if (commonProfile instanceof TokenProfile) {
                tokenProfile = (TokenProfile) commonProfile;
            } else {
                tokenProfile = new TokenProfile();
                Set<String> permissions = commonProfile.getPermissions();
                tokenProfile.addPermissions(permissions);
                Set<String> roles = commonProfile.getRoles();
                tokenProfile.addRoles(roles);
                tokenProfile.setId(commonProfile.getId());
                tokenProfile.addAttributes(commonProfile.getAttributes());
            }
            tokenProfile.setToken(credentials.getToken());
            return tokenProfile;
        }
        return null;
    }

    @Override
    public String generateToken(TokenProfile profile, String expireTime) {
        JwtGenerator<TokenProfile> jwtGenerator = new JwtGenerator<>(signatureConfiguration, encryptionConfiguration);
        if (CommonHelper.isNotBlank(expireTime)) {
            jwtGenerator.setExpirationTime(ExpireTimeUtil.getTargetDate(expireTime));
        }
        return jwtGenerator.generate(profile);
    }
}