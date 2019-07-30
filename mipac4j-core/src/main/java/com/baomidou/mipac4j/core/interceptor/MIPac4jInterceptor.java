package com.baomidou.mipac4j.core.interceptor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.filter.Pac4jFilter;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author miemie
 * @since 2019-07-29
 */
@Data
@Accessors(chain = true)
public class MIPac4jInterceptor implements HandlerInterceptor, InitializingBean {

    private List<Pac4jFilter> filterList = Collections.emptyList();

    private SessionStore<J2EContext> sessionStore;

    private J2EContextFactory j2EContextFactory;

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
        filterList = filterList.stream().peek(Pac4jFilter::initCheck)
                .sorted(Comparator.comparingInt(Pac4jFilter::order)).collect(Collectors.toList());
    }
}
