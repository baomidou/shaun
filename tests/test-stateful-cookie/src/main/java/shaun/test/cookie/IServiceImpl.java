package shaun.test.cookie;

import com.baomidou.shaun.core.mgt.SecurityManager;
import com.baomidou.shaun.core.profile.TokenProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author miemie
 * @since 2020-06-10
 */
@Service
@RequiredArgsConstructor
public class IServiceImpl implements IService {

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
