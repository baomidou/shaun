package com.baomidou.shaun.core.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.springframework.lang.NonNull;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.baomidou.shaun.core.context.J2EContextFactory;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author miemie
 * @since 2019-07-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MIPac4jFilter extends OncePerRequestFilter {

    private List<ShaunFilter> filterList = Collections.emptyList();

    private SessionStore sessionStore;

    private J2EContextFactory j2EContextFactory;

    @SuppressWarnings("unchecked")
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!CorsUtils.isPreFlightRequest(request)) {
            final J2EContext context = j2EContextFactory.applyContext(request, response, sessionStore);
            for (ShaunFilter filter : filterList) {
                if (!filter.goOnChain(context)) {
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected void initFilterBean() throws ServletException {
        filterList = filterList.stream().peek(ShaunFilter::initCheck)
                .sorted(Comparator.comparingInt(ShaunFilter::order)).collect(Collectors.toList());
    }
}
