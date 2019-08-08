package shaun.test.cookie;

import org.pac4j.core.profile.CommonProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.baomidou.shaun.core.annotation.RequirePermissions;
import com.baomidou.shaun.core.annotation.RequireRoles;
import com.baomidou.shaun.core.mgt.SecurityManager;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-08-04
 */
@AllArgsConstructor
@Controller
public class TestController {

    private final SecurityManager securityManager;

    @GetMapping("auth/login")
    public String authLogin() {
        CommonProfile profile = new CommonProfile();
        profile.setId("111111111111");
        profile.addRole("admin");
        profile.addPermission("add");
        securityManager.login(profile);
        return "redirect:/index";
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
    @RequireRoles("admin")
    public String a2(Model model) {
        model.addAttribute("a", "a2");
        return "a";
    }

    @GetMapping("/a3")
    @RequirePermissions("add")
    public String a3(Model model) {
        model.addAttribute("a", "a3");
        return "a";
    }
}
