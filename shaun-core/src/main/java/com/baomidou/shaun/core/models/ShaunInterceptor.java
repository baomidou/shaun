package com.baomidou.shaun.core.models;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.BadRequestAction;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.context.ProfileHolder;
import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.util.WebUtil;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author miemie
 * @since 2019-07-29
 */
@Data
@Accessors(chain = true)
public class ShaunInterceptor implements HandlerInterceptor, InitializingBean {

    private List<ShaunFilter> filterList = Collections.emptyList();
    private Config config;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final JEEContext context = WebUtil.getJEEContext(request, response);
        if (config.getMatchingChecker().matches(context, config.getMatcherNames(), config.getMatchersMap())) {
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

    @Override
    public void afterPropertiesSet() throws Exception {
        filterList = filterList.stream().peek(ShaunFilter::initCheck)
                .sorted(Comparator.comparingInt(ShaunFilter::order)).collect(Collectors.toList());
    }
}
