package com.baomidou.mipac4j.core.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.baomidou.mipac4j.core.context.J2EContextFactory;
import com.baomidou.mipac4j.core.engine.LogoutExecutor;
import com.baomidou.mipac4j.core.engine.Pac4jPlusSecurityLogic;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author miemie
 * @since 2019-07-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Pac4jPlusFilter extends OncePerRequestFilter {

    private SecurityLogic<Boolean, J2EContext> securityLogic = new Pac4jPlusSecurityLogic<>();

    private String authorizers;

    private String matchers;

    private Config config;

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
            final J2EContext context = j2EContextFactory.applyContext(request, response, config.getSessionStore());
            if (securityLogic.perform(context, config, (ctx, pf, parameters) -> true, config.getHttpActionAdapter(),
                    config.getClients().getDefaultSecurityClients(),
                    authorizers, matchers, false)) {
                if (logoutMatcher.matches(context)) {
                    List<CommonProfile> profiles = config.getProfileManagerFactory().apply(context).getAll(false);
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
