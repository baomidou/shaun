package com.baomidou.shaun.core.interceptor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.JEEContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.baomidou.shaun.core.filter.ShaunFilter;
import com.baomidou.shaun.core.util.JEEContextFactory;

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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!CorsUtils.isPreFlightRequest(request)) {
            final JEEContext context = JEEContextFactory.getJEEContext(request, response);
            for (ShaunFilter filter : filterList) {
                if (!filter.goOnChain(context)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        filterList = filterList.stream().peek(ShaunFilter::initCheck)
                .sorted(Comparator.comparingInt(ShaunFilter::order)).collect(Collectors.toList());
    }
}
