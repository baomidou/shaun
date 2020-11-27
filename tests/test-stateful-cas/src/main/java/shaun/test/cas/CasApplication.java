package shaun.test.cas;

import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.UserProfile;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.baomidou.shaun.core.handler.CallbackHandler;

/**
 * @author miemie
 * @since 2020-11-28
 */
@SpringBootApplication
public class CasApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasApplication.class, args);
    }

    @Bean
    public CasClient casClient() {
        CasConfiguration configuration = new CasConfiguration("https://127.0.0.1/cas/login");
        return new CasClient(configuration);
    }

    @Bean
    public CallbackHandler callbackHandler() {
        return new CallbackHandler() {
            @Override
            public void callBack(JEEContext context, UserProfile profile) {
                System.out.println(profile);
            }
        };
    }
}
