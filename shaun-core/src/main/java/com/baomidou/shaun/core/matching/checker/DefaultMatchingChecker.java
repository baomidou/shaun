package com.baomidou.shaun.core.matching.checker;

import com.baomidou.shaun.core.matching.checker.csrf.CsrfTokenGeneratorMatcher;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.matching.checker.MatchingChecker;
import org.pac4j.core.matching.matcher.*;
import org.pac4j.core.matching.matcher.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author miemie
 * @since 2020-05-22
 */
public class DefaultMatchingChecker implements MatchingChecker {

    static final String NO_GET_KEY = "noGet";
    static final Matcher NO_GET = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.POST, HttpConstants.HTTP_METHOD.PUT, HttpConstants.HTTP_METHOD.DELETE);

    static final String ONLY_POST_KEY = "onlyPost";
    static final Matcher ONLY_POST = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.POST);

    static final StrictTransportSecurityMatcher STRICT_TRANSPORT_MATCHER = new StrictTransportSecurityMatcher();
    static final XContentTypeOptionsMatcher X_CONTENT_TYPE_OPTIONS_MATCHER = new XContentTypeOptionsMatcher();
    static final XFrameOptionsMatcher X_FRAME_OPTIONS_MATCHER = new XFrameOptionsMatcher();
    static final XSSProtectionMatcher XSS_PROTECTION_MATCHER = new XSSProtectionMatcher();
    static final CacheControlMatcher CACHE_CONTROL_MATCHER = new CacheControlMatcher();
    static final CsrfTokenGeneratorMatcher CSRF_TOKEN_MATCHER = new CsrfTokenGeneratorMatcher(new DefaultCsrfTokenGenerator());

    @Override
    public boolean matches(final WebContext context, final String matchersValue, final Map<String, Matcher> matchersMap) {
        String matcherNames = matchersValue;
        // by default, if we have no matchers defined, we apply the security headers and the CSRF token generation
        if (CommonHelper.isBlank(matcherNames)) {
            matcherNames = DefaultMatchers.SECURITYHEADERS + Pac4jConstants.ELEMENT_SEPARATOR + DefaultMatchers.CSRF_TOKEN;
        }
        final List<Matcher> matchers = new ArrayList<>();
        // we must have matchers
        CommonHelper.assertNotNull("matchersMap", matchersMap);
        final Map<String, Matcher> allMatchers = buildAllMatchers(matchersMap);
        final String[] names = matcherNames.split(Pac4jConstants.ELEMENT_SEPARATOR);
        final int nb = names.length;
        for (int i = 0; i < nb; i++) {
            final String name = names[i].trim();
            if (DefaultMatchers.HSTS.equalsIgnoreCase(name)) {
                matchers.add(STRICT_TRANSPORT_MATCHER);
            } else if (ONLY_POST_KEY.equalsIgnoreCase(name)) {
                matchers.add(ONLY_POST);
            } else if (NO_GET_KEY.equalsIgnoreCase(name)) {
                matchers.add(NO_GET);
            } else if (DefaultMatchers.NOSNIFF.equalsIgnoreCase(name)) {
                matchers.add(X_CONTENT_TYPE_OPTIONS_MATCHER);
            } else if (DefaultMatchers.NOFRAME.equalsIgnoreCase(name)) {
                matchers.add(X_FRAME_OPTIONS_MATCHER);
            } else if (DefaultMatchers.XSSPROTECTION.equalsIgnoreCase(name)) {
                matchers.add(XSS_PROTECTION_MATCHER);
            } else if (DefaultMatchers.NOCACHE.equalsIgnoreCase(name)) {
                matchers.add(CACHE_CONTROL_MATCHER);
            } else if (DefaultMatchers.SECURITYHEADERS.equalsIgnoreCase(name)) {
                matchers.add(CACHE_CONTROL_MATCHER);
                matchers.add(X_CONTENT_TYPE_OPTIONS_MATCHER);
                matchers.add(STRICT_TRANSPORT_MATCHER);
                matchers.add(X_FRAME_OPTIONS_MATCHER);
                matchers.add(XSS_PROTECTION_MATCHER);
            } else if (DefaultMatchers.CSRF_TOKEN.equalsIgnoreCase(name)) {
                matchers.add(CSRF_TOKEN_MATCHER);
            } else if (!DefaultMatchers.NONE.equalsIgnoreCase(name)) {
                Matcher result = null;
                for (final Map.Entry<String, Matcher> entry : allMatchers.entrySet()) {
                    if (CommonHelper.areEqualsIgnoreCaseAndTrim(entry.getKey(), name)) {
                        result = entry.getValue();
                        break;
                    }
                }
                // we must have a matcher defined for this name
                CommonHelper.assertNotNull("allMatchers['" + name + "']", result);
                matchers.add(result);
            }
        }
        if (!matchers.isEmpty()) {
            // check matching using matchers: all must be satisfied
            for (Matcher matcher : matchers) {
                if (!matcher.matches(context)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Map<String, Matcher> buildAllMatchers(final Map<String, Matcher> matchersMap) {
        return new HashMap<>(matchersMap);
    }
}
