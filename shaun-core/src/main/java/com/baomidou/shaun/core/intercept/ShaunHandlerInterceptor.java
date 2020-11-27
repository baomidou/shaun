package com.baomidou.shaun.core.intercept;

import com.baomidou.shaun.core.config.ShaunConfig;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.intercept.support.ShaunFilterChain;
import com.baomidou.shaun.core.util.WebUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.BadRequestAction;
import org.springframework.lang.NonNull;
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
@Accessors(chain = true)
public class ShaunHandlerInterceptor implements HandlerInterceptor {

    private final List<ShaunFilter> filterList;
    private final ShaunConfig config;

    public ShaunHandlerInterceptor(ShaunConfig config, ShaunFilterChain filterChain) {
        this.config = config;
        this.filterList = filterChain.getOrderFilter();
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        final JEEContext context = WebUtil.getJEEContext(request, response, config.isSessionOn());
        if (config.getMatchingChecker().matches(context, config.getMatcherNames(), config.getMatchersMap(), Collections.emptyList())) {
            if (!CorsUtils.isPreFlightRequest(request)) {
                for (ShaunFilter filter : filterList) {
                    try {
                        if (!filter.goOnChain(config, context)) {
                            return false;
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
        return true;
    }
}
