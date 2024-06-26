/*
 * Copyright 2019-2024 baomidou (wonderming@vip.qq.com)
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
package com.baomidou.shaun.core.filter;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.util.HttpActionInstance;
import com.baomidou.shaun.core.util.WebUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

/**
 * @author miemie
 * @since 2022-06-06
 */
@Slf4j
public class ActuatorFilter extends AbstractShaunFilter {
    private final static String prefix = "Basic ";
    @Setter
    private String username;
    @Setter
    private String password;

    private boolean checkAuth = false;

    public ActuatorFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    protected HttpAction matchThen(CoreConfig config, CallContext context) {
        if (checkAuth) {
            Optional<String> header = WebUtil.getJEEContext(context).getRequestHeader(HttpHeaders.AUTHORIZATION);
            if (header.isEmpty()) {
                return HttpActionInstance.UNAUTHORIZED;
            }
            String auth = header.get();
            if (!auth.startsWith(prefix)) {
                return HttpActionInstance.UNAUTHORIZED;
            }
            auth = auth.substring(prefix.length());
            String basicAuth = HttpHeaders.encodeBasicAuth(username, password, null);
            if (!basicAuth.equals(auth)) {
                return HttpActionInstance.UNAUTHORIZED;
            }
        }
        return null;
    }

    @Override
    public int order() {
        return 400;
    }

    @Override
    public void initCheck() {
        if (username != null || password != null) {
            CommonHelper.assertNotBlank(username, "username");
            CommonHelper.assertNotBlank(password, "password");
            this.checkAuth = true;
        }
    }
}
