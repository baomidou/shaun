package shaun.test.cookie;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * @author miemie
 * @since 2019-08-04
 */
@SpringBootApplication
public class Cookie2Application {

    public static void main(String[] args) {
        SpringApplication.run(Cookie2Application.class, args);
    }

    @Bean
    public Authorizer<TokenProfile> authorizer1() {
        return (context, profiles) -> {
            final TokenProfile profile = profiles.get(0);
            profile.setLinkedId("111222333444555666777888999000");
            return true;
        };
    }
}
