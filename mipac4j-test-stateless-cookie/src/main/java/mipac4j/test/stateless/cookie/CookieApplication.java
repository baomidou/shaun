package mipac4j.test.stateless.cookie;

import org.pac4j.core.context.HttpConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.baomidou.mipac4j.core.context.cookie.CookieContext;
import com.baomidou.mipac4j.core.context.http.DoHttpAction;
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

    @Bean
    public DoHttpAction doHttpAction() {
        return (code, context) -> {
            if (code == HttpConstants.UNAUTHORIZED) {
                context.setResponseContentType("text/html;charset=UTF-8");
                context.writeResponseContent("你没有权限");
            }
        };
    }
}
