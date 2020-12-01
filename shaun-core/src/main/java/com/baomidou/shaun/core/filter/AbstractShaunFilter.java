package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.CoreConfig;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;

/**
 * @author miemie
 * @since 2020-12-01
 */
public abstract class AbstractShaunFilter implements ShaunFilter {

    private final Matcher pathMatcher;

    public AbstractShaunFilter(Matcher pathMatcher) {
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        this.pathMatcher = pathMatcher;
    }

    @Override
    final public HttpAction doFilter(CoreConfig config, JEEContext context) {
        if (pathMatcher.matches(context)) {
            try {
                return matchThen(config, context);
            } catch (Exception ex) {
                return handleEx(ex);
            }
        }
        return null;
    }

    protected abstract HttpAction matchThen(CoreConfig config, JEEContext context);

    protected HttpAction handleEx(Exception e) {
        if (e instanceof HttpAction) {
            return (HttpAction) e;
        }
        throw new RuntimeException(e);
    }
}
