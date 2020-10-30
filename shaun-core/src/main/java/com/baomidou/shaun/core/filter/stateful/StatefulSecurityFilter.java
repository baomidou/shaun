package com.baomidou.shaun.core.filter.stateful;

import com.baomidou.shaun.core.config.Config;
import com.baomidou.shaun.core.filter.SecurityFilter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.matching.matcher.Matcher;

/**
 * security filter
 *
 * @author miemie
 * @since 2019-07-24
 */
@Slf4j
public class StatefulSecurityFilter extends SecurityFilter {

    public StatefulSecurityFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    protected void fail(Config config, JEEContext context) {
        if (config.getAjaxRequestResolver().isAjax(context)) {
            config.getHttpActionHandler().preHandle(UnauthorizedAction.INSTANCE, context);
        } else {
            config.redirectLoginUrl(context);
        }
    }
}
