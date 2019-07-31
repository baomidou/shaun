package shaun.test.cookie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.baomidou.shaun.core.context.cookie.CookieContext;
import com.baomidou.shaun.core.engine.LogoutExecutor;

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
    public LogoutExecutor logoutExecutor(CookieContext cookieContext) {
        return (ctx, profiles) -> cookieContext.clearCookie();
    }
}
