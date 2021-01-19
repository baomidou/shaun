package shaun.test.stateless.togglz;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.togglz.ShaunUserProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.togglz.core.user.UserProvider;
import shaun.test.support.StarterWebInfo;

/**
 * @author miemie
 * @since 2019-07-25
 */
@SpringBootApplication
public class TogglzApplication {

    public static void main(String[] args) {
        SpringApplication.run(TogglzApplication.class, args);
    }

    @Bean
    public UserProvider userProvider(CoreConfig config) {
        return new ShaunUserProvider(config, "featureAdmin", i -> {
            System.out.println("!!!!!走到我了!!!!!");
            return i.getId();
        });
    }

    @Component
    public static class Show extends StarterWebInfo implements CommandLineRunner {

        @Override
        public void run(String... args) throws Exception {
            logUrl(null, "/login");
        }
    }
}
