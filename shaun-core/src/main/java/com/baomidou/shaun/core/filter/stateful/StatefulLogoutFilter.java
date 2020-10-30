package com.baomidou.shaun.core.filter.stateful;

import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.filter.LogoutFilter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.matching.matcher.Matcher;

/**
 * logout filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Slf4j
public class StatefulLogoutFilter extends LogoutFilter {

    public StatefulLogoutFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    protected void logoutThen(Config config, JEEContext context) {
        if (!config.getAjaxRequestResolver().isAjax(context)) {
            config.redirectLoginUrl(context);
        }
    }
}
