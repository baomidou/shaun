package com.baomidou.shaun.core.handler.logout;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;

import com.baomidou.shaun.core.profile.ProfileContext;

import lombok.AllArgsConstructor;
import lombok.Data;

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
    public void logout(J2EContext context, CommonProfile profile) {
        profileContext.logout(context);
        client.redirect(context); // 不抛出这个异常
    }
}
