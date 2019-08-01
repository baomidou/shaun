package com.baomidou.shaun.core.handler.logout;

import com.baomidou.shaun.core.profile.ProfileContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * @author miemie
 * @since 2019-07-31
 */
@Data
@AllArgsConstructor
public class SessionLogoutHandler implements LogoutHandler<CommonProfile> {

    private final ProfileContext profileContext;
    private final IndirectClient client;

    @Override
    public void logout(JEEContext context, CommonProfile profile) {
        profileContext.logout(context);
        client.redirect(context); // 不抛出这个异常
    }
}
