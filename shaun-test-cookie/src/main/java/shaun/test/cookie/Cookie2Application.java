package shaun.test.cookie;

import com.baomidou.shaun.core.context.cookie.CookieContext;
import com.baomidou.shaun.core.handler.LogoutHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author miemie
 * @since 2019-07-25
 */
@SpringBootApplication
public class Cookie2Application {

    public static void main(String[] args) {
        SpringApplication.run(Cookie2Application.class, args);
    }

    @Bean
    public LogoutHandler logoutExecutor(CookieContext cookieContext) {
        return (ctx, profiles) -> cookieContext.clearCookie();
    }
}
