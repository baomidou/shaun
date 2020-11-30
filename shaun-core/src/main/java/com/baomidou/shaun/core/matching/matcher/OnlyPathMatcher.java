/**
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
