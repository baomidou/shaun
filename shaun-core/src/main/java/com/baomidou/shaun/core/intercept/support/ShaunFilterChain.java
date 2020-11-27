package com.baomidou.shaun.core.intercept.support;

import com.baomidou.shaun.core.filter.ShaunFilter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author miemie
 * @since 2020-10-30
 */
public interface ShaunFilterChain {

    List<ShaunFilter> getFilterChain();

    default List<ShaunFilter> getOrderFilter() {
        return getFilterChain().stream().peek(ShaunFilter::initCheck)
                .sorted(Comparator.comparingInt(ShaunFilter::order)).collect(Collectors.toList());
    }
}
