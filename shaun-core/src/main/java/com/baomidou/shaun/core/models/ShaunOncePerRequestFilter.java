package com.baomidou.shaun.core.models;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.JEEContext;
import org.springframework.lang.NonNull;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.util.JEEContextFactory;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author miemie
 * @since 2019-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShaunOncePerRequestFilter extends OncePerRequestFilter {

    private List<ShaunFilter> filterList = Collections.emptyList();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        if (!CorsUtils.isPreFlightRequest(request)) {
            final JEEContext context = JEEContextFactory.getJEEContext(request, response);
            for (ShaunFilter filter : filterList) {
                if (!filter.goOnChain(context)) {
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    protected void initFilterBean() throws ServletException {
        filterList = filterList.stream().peek(ShaunFilter::initCheck)
                .sorted(Comparator.comparingInt(ShaunFilter::order)).collect(Collectors.toList());
    }
}
