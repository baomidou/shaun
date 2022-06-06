package shaun.test.actuator;

import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import shaun.test.support.StarterWebInfo;

/**
 * @author miemie
 * @since 2019-08-04
 */
@SpringBootApplication
public class ActuatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActuatorApplication.class, args);
    }

    @Slf4j
    @Component
    @RequiredArgsConstructor
    public static class Show extends StarterWebInfo implements CommandLineRunner {

        private final ShaunProperties properties;

        @Override
        public void run(String... args) throws Exception {
            logUrl(properties.getCookie().getDomain(), "/index");
        }
    }
}
