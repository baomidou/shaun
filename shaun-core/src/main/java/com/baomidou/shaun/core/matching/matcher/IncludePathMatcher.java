/*
 * Copyright 2019-2022 baomidou (wonderming@vip.qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.core.matching.matcher;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author miemie
 * @since 2022-06-06
 */
public class IncludePathMatcher implements Matcher {

    private final Set<String> includePaths = new HashSet<>();
    private final Set<Pattern> includePatterns = new HashSet<>();

    @Override
    public boolean matches(CallContext context) {
        String path = context.webContext().getPath();

        if (includePaths.contains(path)) {
            return true;
        }

        return includePatterns.stream().anyMatch(i -> i.matcher(path).matches());
    }

    public IncludePathMatcher includePath(final String path) {
        validatePath(path);
        includePaths.add(path);
        return this;
    }

    public IncludePathMatcher includePaths(final String... paths) {
        if (paths != null && paths.length > 0) {
            for (final String path : paths) {
                includePath(path);
            }
        }
        return this;
    }

    public IncludePathMatcher includeBranch(final String path) {
        validatePath(path);
        includePatterns.add(Pattern.compile("^" + path + "(/.*)?$"));
        return this;
    }

    public IncludePathMatcher includeRegex(final String regex) {
        CommonHelper.assertNotBlank("regex", regex);

        if (!regex.startsWith("^") || !regex.endsWith("$")) {
            throw new TechnicalException("Your regular expression: '" + regex + "' must start with a ^ and end with a $ " +
                    "to define a full path matching");
        }

        includePatterns.add(Pattern.compile(regex));
        return this;
    }

    private void validatePath(String path) {
        CommonHelper.assertNotBlank("path", path);
        if (!path.startsWith("/")) {
            throw new TechnicalException("path must begin with a /");
        }
    }
}
