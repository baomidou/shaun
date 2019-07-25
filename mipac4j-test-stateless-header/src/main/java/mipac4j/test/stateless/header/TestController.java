package mipac4j.test.stateless.header;

import org.pac4j.core.profile.CommonProfile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mipac4j.core.annotation.RequireAnyPermission;
import com.baomidou.mipac4j.core.annotation.RequireAnyRole;
import com.baomidou.mipac4j.core.generator.TokenGenerator;

import lombok.AllArgsConstructor;

/**
 * @author miemie
 * @since 2019-07-25
 */
@AllArgsConstructor
@RestController
public class TestController {

    private final TokenGenerator tokenGenerator;

    @GetMapping("login")
    public String login() {
        CommonProfile profile = new CommonProfile();
        profile.setId("111111111111");
        profile.setLinkedId("22222222222");
        profile.addRole("admin");
        profile.addPermission("add");
        return tokenGenerator.generate(profile);
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
