package com.baomidou.shaun.core.models;

import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.filter.chain.ShaunFilterChain;
import com.baomidou.shaun.core.util.WebUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.BadRequestAction;
import org.pac4j.core.matching.checker.DefaultMatchingChecker;
import org.pac4j.core.matching.checker.MatchingChecker;
import org.springframework.lang.NonNull;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author miemie
 * @since 2019-08-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ShaunRequestFilter extends OncePerRequestFilter {

    private final Config config;
    private final List<ShaunFilter> filterList;
    private MatchingChecker matchingChecker = new DefaultMatchingChecker();

    public ShaunRequestFilter(Config config, ShaunFilterChain filterChain) {
        this.config = config;
        this.filterList = filterChain.getOrderFilter();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        final JEEContext context = WebUtil.getJEEContext(request, response, config.isSessionOn());
        if (matchingChecker.matches(context, config.getMatcherNames(), config.getMatchersMap(), Collections.emptyList())) {
            if (!CorsUtils.isPreFlightRequest(request)) {
                for (ShaunFilter filter : filterList) {
                    try {
                        if (!filter.goOnChain(config, context)) {
                            return;
                        }
                    } catch (Exception e) {
                        ProfileHolder.clearProfile();
                        throw e;
                    }
                }
            }
        } else {
            config.getHttpActionHandler().preHandle(BadRequestAction.INSTANCE, context);
        }
        chain.doFilter(request, response);
    }
}
