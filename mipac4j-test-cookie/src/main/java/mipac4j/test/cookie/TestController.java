package mipac4j.test.cookie;

import com.baomidou.mipac4j.core.annotation.RequireAnyPermission;
import com.baomidou.mipac4j.core.annotation.RequireAnyRole;
import com.baomidou.mipac4j.core.context.cookie.CookieContext;
import lombok.AllArgsConstructor;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author miemie
 * @since 2019-07-25
 */
@AllArgsConstructor
@Controller
public class TestController {

    private final CookieContext cookieContext;

    @GetMapping("auth/login")
    public String authLogin() {
        CommonProfile profile = new CommonProfile();
        profile.setId("111111111111");
        profile.setLinkedId("22222222222");
        profile.addRole("admin");
        profile.addPermission("add");
        cookieContext.generateAndAddCookie(profile);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/a1")
    public String a1(Model model) {
        model.addAttribute("a", "a1");
        return "a";
    }

    @GetMapping("/a2")
    @RequireAnyRole("admin")
    public String a2(Model model) {
        model.addAttribute("a", "a2");
        return "a";
    }

    @GetMapping("/a3")
    @RequireAnyPermission("add")
    public String a3(Model model) {
        model.addAttribute("a", "a3");
        return "a";
    }
}
