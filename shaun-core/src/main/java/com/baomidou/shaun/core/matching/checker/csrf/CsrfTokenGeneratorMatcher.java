package com.baomidou.shaun.core.matching.checker.csrf;

import lombok.Data;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.csrf.CsrfTokenGenerator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

/**
 * @author miemie
 * @since 2020-05-22
 */
@Data
public class CsrfTokenGeneratorMatcher implements Matcher {

    private CsrfTokenGenerator csrfTokenGenerator;

    public CsrfTokenGeneratorMatcher(final CsrfTokenGenerator csrfTokenGenerator) {
        this.csrfTokenGenerator = csrfTokenGenerator;
    }

    @Override
    public boolean matches(final WebContext context) {
        CommonHelper.assertNotNull("csrfTokenGenerator", csrfTokenGenerator);
        final String token = csrfTokenGenerator.get(context);
        context.setRequestAttribute(Pac4jConstants.CSRF_TOKEN, token);
        return true;
    }
}
