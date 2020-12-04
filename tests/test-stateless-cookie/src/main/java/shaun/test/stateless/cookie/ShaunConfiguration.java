package shaun.test.stateless.cookie;

import com.baomidou.shaun.core.jwt.DefaultJwtModelSelector;
import com.baomidou.shaun.core.jwt.JwtModelSelector;
import com.baomidou.shaun.core.mgt.ProfileStateManager;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import org.pac4j.jwt.config.encryption.RSAEncryptionConfiguration;
import org.pac4j.jwt.config.signature.RSASignatureConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author miemie
 * @since 2019-10-29
 */
@Configuration
public class ShaunConfiguration {

    /**
     * 模拟缓存
     */
    public static Map<String, String> tokenMap = new HashMap<>();

    private static final KeyPair keyPair = initKeyPair();

    static KeyPair initKeyPair() {
        final KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public JwtModelSelector jwtModelSelector() {
        return new DefaultJwtModelSelector(new RSASignatureConfiguration(keyPair),
                new RSAEncryptionConfiguration(keyPair, JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM));
    }

    @Bean
    public ProfileStateManager profileStateManager() {
        return new ProfileStateManager() {
            @Override
            public void online(TokenProfile profile) {
                tokenMap.put(profile.getId(), profile.getToken());
            }

            @Override
            public boolean isOnline(TokenProfile profile) {
                String token = tokenMap.get(profile.getId());
                return profile.getToken().equals(token);
            }

            @Override
            public void offline(TokenProfile profile) {
                tokenMap.remove(profile.getId());
            }
        };
    }
}