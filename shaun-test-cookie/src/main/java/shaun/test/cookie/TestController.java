package shaun.test.cookie;

import com.baomidou.shaun.core.annotation.HasPermission;
import com.baomidou.shaun.core.annotation.HasRole;
import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.profile.TokenProfile;
import com.baomidou.shaun.core.util.JEEContextUtil;
import com.baomidou.shaun.core.util.ProfileHolder;
import lombok.AllArgsConstructor;
import org.pac4j.core.util.Pac4jConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

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
        TokenProfile profile = new TokenProfile();
        profile.setId("111111111111");
        profile.addRole("admin");
        profile.addPermission("add");
        profile.addPermission("xx");
        securityManager.login(profile);
        return "redirect:/index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/index")
    public String index() {
        TokenProfile profile = ProfileHolder.getProfile();
        System.out.println(profile.getId());
        System.out.println(profile.getToken());
        System.out.println(profile.getExpirationDate());
        System.out.println(profile.getIssuedAt());
        System.out.println(profile.getLinkedId());
        return "index";
    }

    @GetMapping("/a1")
    public String a1(Model model) {
        model.addAttribute("a", "a1");
        model.addAttribute("csrf", JEEContextUtil.getJEEContext().getRequestAttribute(Pac4jConstants.CSRF_TOKEN).orElse(null));
        return "a";
    }

    @GetMapping("/a2")
    @HasRole("admin")
    public String a2(Model model) {
        model.addAttribute("a", "a2");
        model.addAttribute("csrf", JEEContextUtil.getJEEContext().getRequestAttribute(Pac4jConstants.CSRF_TOKEN).orElse(null));
        return "a";
    }

    @GetMapping("/a3")
    @HasPermission("add")
    public String a3(Model model) {
        model.addAttribute("a", "a3");
        model.addAttribute("csrf", JEEContextUtil.getJEEContext().getRequestAttribute(Pac4jConstants.CSRF_TOKEN).orElse(null));
        return "a";
    }

    @ResponseBody
    @PostMapping("/a4")
    public String a4() {
        return "a4_" + UUID.randomUUID();
    }
}
