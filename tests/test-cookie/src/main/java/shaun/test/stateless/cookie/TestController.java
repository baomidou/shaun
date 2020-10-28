package shaun.test.stateless.cookie;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.shaun.core.annotation.HasPermission;
import com.baomidou.shaun.core.annotation.HasRole;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.profile.TokenProfile;

import lombok.AllArgsConstructor;

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
//        profile.setLinkedId("222222222222"); 不支持这个属性
        profile.addRole("admin");
//        profile.addPermission("add");
        return securityManager.login(profile, false);
    }

    @GetMapping("a1")
    public String a1() {
        return "a1";
    }

    @GetMapping("a2")
    @HasRole("admin")
    public String a2() {
        return "a2";
    }

    @GetMapping("a3")
    @HasPermission("add")
    public String a3() {
        return "a3";
    }

    @GetMapping("xx")
    public String xx() {
        ShaunConfig.tokenMap.remove(ProfileHolder.getProfile().getId());
        return "xx";
    }
}
