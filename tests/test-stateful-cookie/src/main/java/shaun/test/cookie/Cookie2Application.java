package shaun.test.cookie;

import java.util.concurrent.ConcurrentHashMap;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.stereotype.Component;

import com.baomidou.shaun.autoconfigure.properties.ShaunProperties;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shaun.test.support.StarterWebInfo;

/**
 * @author miemie
 * @since 2019-08-04
 */
@EnableSpringHttpSession
@SpringBootApplication
public class Cookie2Application {

    public static void main(String[] args) {
        System.setProperty(ProfileHolder.SYSTEM_PROPERTY, ProfileHolder.MODE_THREADLOCAL); // 设置存储Profile的模式
        SpringApplication.run(Cookie2Application.class, args);
    }

    @Bean
    public MapSessionRepository sessionRepository() {
        return new MapSessionRepository(new ConcurrentHashMap<>());
    }

    @Bean
    public Authorizer<TokenProfile> authorizer1() {
        return (context, profiles) -> {
            final TokenProfile profile = profiles.get(0);
            profile.setLinkedId("111222333444555666777888999000");
            return true;
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public MyAspect myAspect() {
        return new MyAspect();
    }

    @Slf4j
    @Component
    @RequiredArgsConstructor
    public static class Show extends StarterWebInfo implements CommandLineRunner {

        private final ShaunProperties properties;

        @Override
        public void run(String... args) throws Exception {
            logUrl(properties.getSecurity().getExtractor().getCookie().getDomain(), "/index");
        }
    }
}
