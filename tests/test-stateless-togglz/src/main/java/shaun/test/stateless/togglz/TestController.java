package shaun.test.stateless.togglz;

import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.profile.TokenProfile;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author miemie
 * @since 2019-07-25
 */
@AllArgsConstructor
@RestController
public class TestController {

    private final SecurityManager securityManager;

    @GetMapping("login")
    public String login() {
        TokenProfile profile = new TokenProfile();
        profile.setId("111111111111");
        profile.setLinkedId("22222222222");
        profile.addRole("admin");
        profile.addPermission("add");
        return securityManager.login(profile, true);
    }

    @GetMapping("a")
    public String a() {
        String result = "";
        if (MyFeatures.ONE.isActive()) {
            result += "ONE is active\n";
        }
        if (MyFeatures.TWO.isActive()) {
            result += "TWO is active\n";
        }
        return result;
    }
}
