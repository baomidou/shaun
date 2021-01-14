/*
 * Copyright 2019-2021 baomidou (wonderming@vip.qq.com)
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
package com.baomidou.shaun.core.handler;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.exception.http.WithContentAction;
import org.pac4j.core.exception.http.WithLocationAction;

import com.baomidou.shaun.core.config.CoreConfig;
import com.baomidou.shaun.core.exception.http.FoundLoginAction;
import com.baomidou.shaun.core.util.WebUtil;

/**
 * @author miemie
 * @since 2019-08-08
 */
public class DefaultHttpActionHandler implements HttpActionHandler {

    @Override
    public void handle(CoreConfig config, JEEContext context, HttpAction action) {
        if (config.isStateless()) {
            handleStateless(config, context, action);
        }
        handleStateful(config, context, action);
    }

    /**
     * 无状态 下处理
     */
    protected void handleStateless(CoreConfig config, JEEContext context, HttpAction action) {
        if (action instanceof RedirectionAction) {
            return;
        }
        throw action;
    }

    /**
     * 有状态 下处理
     */
    protected void handleStateful(CoreConfig config, JEEContext context, HttpAction action) {
        if (config.getAjaxRequestResolver().isAjax(context)) {
            handleStatefulAjax(config, context, action);
        } else {
            handleStatefulNotAjax(config, context, action);
        }
    }

    /**
     * 有状态 下处理 ajax
     */
    protected void handleStatefulAjax(CoreConfig config, JEEContext context, HttpAction action) {
        throw action;
    }

    /**
     * 有状态 下处理 非ajax
     */
    protected void handleStatefulNotAjax(CoreConfig config, JEEContext context, HttpAction action) {
        if (action instanceof UnauthorizedAction || action instanceof ForbiddenAction ||
                action instanceof FoundLoginAction) {
            WebUtil.redirectUrl(context, config.getLoginUrl());
        } else if (action instanceof WithLocationAction) {
            WebUtil.redirectUrl(context, action.getCode(), ((WithLocationAction) action).getLocation());
        } else if (action instanceof WithContentAction) {
            WebUtil.write(context, action.getCode(), ((WithContentAction) action).getContent());
        } else {
            throw action;
        }
    }
}
