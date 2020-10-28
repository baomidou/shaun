package shaun.test.cookie;

import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.profile.TokenProfile;

import lombok.RequiredArgsConstructor;

/**
 * @author miemie
 * @since 2020-06-10
 */
@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    private final SecurityManager securityManager;

    @Override
    public void login() {
        TokenProfile profile = new TokenProfile();
        profile.setId("111111111111");
        profile.addRole("admin");
        profile.addPermission("add");
        profile.addPermission("xx");
        securityManager.login(profile);
    }
}
