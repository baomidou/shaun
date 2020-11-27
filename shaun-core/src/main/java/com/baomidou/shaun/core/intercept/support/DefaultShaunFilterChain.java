package com.baomidou.shaun.core.intercept.support;

import com.baomidou.shaun.core.filter.ShaunFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author miemie
 * @since 2020-10-30
 */
public class DefaultShaunFilterChain implements ShaunFilterChain {

    private final List<ShaunFilter> filterList = new ArrayList<>();

    public void addShaunFilter(ShaunFilter filter) {
        filterList.add(filter);
    }

    @Override
    public List<ShaunFilter> getFilterChain() {
        return filterList;
    }
}
