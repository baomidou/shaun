package shaun.test.stateless.cookie;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.RSAEncryptionConfiguration;
import org.pac4j.jwt.config.signature.RSASignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * @author miemie
 * @since 2019-10-29
 */
@Configuration
public class ShaunConfig {

    @Bean
    public KeyPair keyPair() throws Exception {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    /**
     * jwt 签名类
     */
    @Bean
    public SignatureConfiguration signatureConfiguration(KeyPair keyPair) {
        return new RSASignatureConfiguration(keyPair);
    }

    /**
     * jwt 加密类
     */
    @Bean
    public EncryptionConfiguration encryptionConfiguration(KeyPair keyPair) {
        return new RSAEncryptionConfiguration(keyPair, JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM);
    }
}
