package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.CoreConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

/**
 * @author miemie
 * @since 2022-06-06
 */
@Slf4j
public class ActuatorFilter extends AbstractShaunFilter {

    @Setter
    private String username;
    @Setter
    private String password;

    private boolean checkAuth = false;

    public ActuatorFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    protected HttpAction matchThen(CoreConfig config, JEEContext context) {
        if (checkAuth) {
            Optional<String> header = context.getRequestHeader(HttpHeaders.AUTHORIZATION);
            if (!header.isPresent()) {
                return UnauthorizedAction.INSTANCE;
            }
            String auth = header.get();
            auth = auth.substring("Basic ".length());
            String basicAuth = HttpHeaders.encodeBasicAuth(username, password, null);
            if (!basicAuth.equals(auth)) {
                return UnauthorizedAction.INSTANCE;
            }
        }
        return null;
    }

    @Override
    public int order() {
        return 400;
    }

    @Override
    public void initCheck() {
        if (username != null || password != null) {
            CommonHelper.assertNotBlank(username, "username");
            CommonHelper.assertNotBlank(password, "password");
            this.checkAuth = true;
        }
    }
}
