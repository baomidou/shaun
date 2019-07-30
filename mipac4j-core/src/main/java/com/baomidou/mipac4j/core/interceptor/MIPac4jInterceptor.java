package com.baomidou.mipac4j.core.interceptor;

import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;
import lombok.Data;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * @author miemie
 * @since 2019-07-29
 */
@Data
public class MIPac4jInterceptor implements HandlerInterceptor, InitializingBean {

    private List<Pac4jFilter> filterList = Collections.emptyList();

    private final SessionStore<J2EContext> sessionStore;￿

    private final J2EContextFactory j2EContextFactory;

    public MIPac4jInterceptor(SessionStore<J2EContext> sessionStore, J2EContextFactory j2EContextFactory) {
        this.sessionStore = sessionStore;
        this.j2EContextFactory = j2EContextFactory;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!CorsUtils.isPreFlightRequest(request)) {
            final J2EContext context = j2EContextFactory.applyContext(request, response, sessionStore);
            for (Pac4jFilter filter : filterList) {
                if (!filter.goOnChain(context)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CommonHelper.assertNotNull("sessionStore", sessionStore);
        CommonHelper.assertNotNull("j2EContextFactory", j2EContextFactory);
    }
}
