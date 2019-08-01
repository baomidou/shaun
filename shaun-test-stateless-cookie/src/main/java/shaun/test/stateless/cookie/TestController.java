package shaun.test.stateless.cookie;

import com.baomidou.shaun.core.annotation.RequireAnyPermission;
import com.baomidou.shaun.core.annotation.RequireAnyRole;
import com.baomidou.shaun.core.context.cookie.CookieContext;
import lombok.AllArgsConstructor;
import org.pac4j.jwt.profile.JwtProfile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author miemie
 * @since 2019-07-25
 */
@AllArgsConstructor
@RestController
public class TestController {

    private final CookieContext cookieContext;

    @GetMapping("login")
    public String login() {
        JwtProfile profile = new JwtProfile();
        profile.setId("111111111111");
//        profile.setLinkedId("222222222222"); 不支持这个属性
        profile.addRole("admin");
//        profile.addPermission("add");
        return cookieContext.generateAndAddCookie(profile);
    }

    @GetMapping("a1")
    public String a1() {
        return "a1";
    }

    @GetMapping("a2")
    @RequireAnyRole("admin")
    public String a2() {
        return "a2";
    }

    @GetMapping("a3")
    @RequireAnyPermission("add")
    public String a3() {
        return "a3";
    }
}
