package com.baomidou.shaun.core.matching.matcher;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;

/**
 * 地址匹配
 *
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

    @Override
    public boolean matches(WebContext context) {
        return path.equals(context.getPath());
    }
}
