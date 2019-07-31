package mipac4j.test.stateless.cookie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.baomidou.mipac4j.core.context.cookie.CookieContext;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;

/**
 * @author miemie
 * @since 2019-07-25
 */
@SpringBootApplication
public class CookieApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookieApplication.class, args);
    }

    @Bean
    public LogoutExecutor logoutExecutor(CookieContext cookieContext) {
        return (ctx, profiles) -> cookieContext.clearCookie();
    }
}
