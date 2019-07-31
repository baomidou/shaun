package com.baomidou.shaun.core.matching;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.util.CommonHelper;

/**
 * @author miemie
 * @since 2019-07-25
 */
public class OnlyPathMatcher implements Matcher {

    /**
     * 需要匹配的路径
     */
    private final String path;

    public OnlyPathMatcher(String path) {
        CommonHelper.assertNotBlank("path", path);
        this.path = path;
    }

    public static OnlyPathMatcher instance(final String path) {
        return new OnlyPathMatcher(path);
    }

    @Override
    public boolean matches(WebContext context) {
        return path.equals(context.getPath());
    }
}
