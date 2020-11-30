package shaun.test.cookie;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.exception.http.HttpAction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.handler.HttpActionAdapter;
import com.baomidou.shaun.core.profile.TokenProfile;

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
    public HttpActionAdapter httpActionHandler() {
        return (config, context, ex) -> {
            HttpServletResponse response = context.getNativeResponse();
            try {
                response.setStatus(((HttpAction) ex).getCode());
                try (ServletOutputStream os = response.getOutputStream()) {
                    os.write("异常".getBytes());
                    os.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public MyAspect myAspect() {
        return new MyAspect();
    }
}
