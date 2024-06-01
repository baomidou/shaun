package shaun.test.cas;

import com.baomidou.shaun.core.annotation.HasPermission;
import com.baomidou.shaun.core.annotation.HasRole;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.profile.TokenProfile;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
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
        return "a";
    }

    @GetMapping("/a2")
    @HasRole("admin")
    public String a2(Model model) {
        model.addAttribute("a", "a2");
        return "a";
    }

    @GetMapping("/a3")
    @HasPermission("add")
    public String a3(HttpServletRequest request, Model model) {
        System.out.println(request);
        model.addAttribute("a", "a3");
        return "a";
    }

    @ResponseBody
    @PostMapping("/a4")
    public String a4() {
        return "a4_" + UUID.randomUUID();
    }
}
