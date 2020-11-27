package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.ShaunConfig;
import org.pac4j.core.context.JEEContext;

/**
 * 内部 filter
 *
 * @author miemie
 * @since 2019-07-24
 */
public interface ShaunFilter {

    /**
     * 是否继续执行 FilterChain.doFilter(request, response);
     *
     * @param config  全局配置
     * @param context webContext
     * @return 是否继续执行
     */
    boolean goOnChain(ShaunConfig config, JEEContext context);

    /**
     * 有多个子类时执行顺序(越小越优先)
     *
     * @return int
     */
    default int order() {
        return 0;
    }

    void initCheck();
}
