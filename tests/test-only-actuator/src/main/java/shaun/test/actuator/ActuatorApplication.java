package shaun.test.actuator;

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

    @Component
    public static class Show extends StarterWebInfo implements CommandLineRunner {

        @Override
        public void run(String... args) throws Exception {
            logUrl(null, "/index");
        }
    }
}
