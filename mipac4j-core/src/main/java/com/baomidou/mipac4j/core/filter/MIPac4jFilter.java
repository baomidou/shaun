package com.baomidou.mipac4j.core.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.engine.CallbackLogic;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.engine.MIPac4jCallbackLogic;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author miemie
 * @since 2019-07-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MIPac4jFilter extends OncePerRequestFilter {

    private SecurityLogic<Boolean, J2EContext> securityLogic = new DefaultSecurityLogic<>();

    private CallbackLogic<Boolean, J2EContext> callbackLogic = new MIPac4jCallbackLogic<>();

    private String authorizers;

    private String matchers;

    private Config securityConfig;

    private Config callbackConfig;

    private String logoutUrl;

    private Matcher logoutMatcher;

    private LogoutExecutor logoutExecutor;

    private J2EContextFactory j2EContextFactory;

    private ListableBeanFactory beanFactory;

    @SuppressWarnings("unchecked")
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!CorsUtils.isPreFlightRequest(request)) {
            final J2EContext context = j2EContextFactory.applyContext(request, response, securityConfig.getSessionStore());
            if (securityLogic.perform(context, securityConfig, (ctx, pf, parameters) -> true, securityConfig.getHttpActionAdapter(),
                    securityConfig.getClients().getDefaultSecurityClients(),
                    authorizers, matchers, false)) {
                if (logoutMatcher.matches(context)) {
                    List<CommonProfile> profiles = securityConfig.getProfileManagerFactory().apply(context).getAll(false);
                    logoutExecutor.logout(context, profiles);
                    return;
                }
            } else {
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected void initFilterBean() throws ServletException {
        if (StringUtils.hasText(logoutUrl)) {
            this.logoutMatcher = ctx -> ctx.getPath().equals(logoutUrl);
        } else {
            this.logoutMatcher = ctx -> false;
        }
    }
}
