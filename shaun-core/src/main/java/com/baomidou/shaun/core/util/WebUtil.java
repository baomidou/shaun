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
package com.baomidou.shaun.core.util;

import com.baomidou.shaun.core.exception.ShaunException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.jee.context.JEEContext;
import org.pac4j.jee.context.session.JEESessionStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author miemie
 * @since 2020-05-26
 */
public abstract class WebUtil {
    private static final JEESessionStore JEE_SESSION_STORE = new JEESessionStore();

    /**
     * 获取 ServletRequestAttributes
     *
     * @return ServletRequestAttributes
     */
    public static ServletRequestAttributes getServletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    /**
     * 获取 request
     *
     * @return request
     */
    public static HttpServletRequest getRequestBySpringWebHolder() {
        return getServletRequestAttributes().getRequest();
    }

    /**
     * 获取 response
     *
     * @return response
     */
    public static HttpServletResponse getResponseBySpringWebHolder() {
        return getServletRequestAttributes().getResponse();
    }

    /**
     * 重定向到指定页面
     *
     * @param context 上下文
     * @param url     地址
     */
    public static void redirectUrl(CallContext context, String url) {
        redirectUrl(context, HttpConstants.FOUND, url);
    }

    /**
     * 重定向到指定页面
     *
     * @param context 上下文
     * @param code    httpCode
     * @param url     地址
     */
    public static void redirectUrl(CallContext context, int code, String url) {
        final HttpServletResponse response = getJEEContext(context).getNativeResponse();
        response.setHeader(HttpConstants.LOCATION_HEADER, url);
        response.setStatus(code);
    }

    /**
     * 写入内容
     *
     * @param context 上下文
     * @param content 信息
     */
    public static void write(CallContext context, String content) {
        write(context, HttpConstants.OK, content);
    }

    /**
     * 写入内容
     *
     * @param context 上下文
     * @param code    httpCode
     * @param content 信息
     */
    public static void write(CallContext context, int code, String content) {
        write(context, code, content, "text/plain");
    }

    /**
     * 写入内容
     *
     * @param context 上下文
     * @param code    httpCode
     * @param content 信息
     */
    public static void write(CallContext context, int code, String content, String contentType) {
        final HttpServletResponse response = getJEEContext(context).getNativeResponse();
        try {
            response.setStatus(code);
            if (content != null) {
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType(contentType);
                response.getWriter().write(content);
            }
            response.flushBuffer();
        } catch (IOException e) {
            throw new ShaunException(e);
        }
    }

    public static JEEContext getJEEContext() {
        ServletRequestAttributes sra = getServletRequestAttributes();
        return getJEEContext(sra.getRequest(), sra.getResponse());
    }

    public static JEEContext getJEEContext(CallContext callContext) {
        return (JEEContext) callContext.webContext();
    }

    public static JEEContext getJEEContext(HttpServletRequest request, HttpServletResponse response) {
        return new JEEContext(request, response);
    }

    public static CallContext getCallContext() {
        ServletRequestAttributes sra = getServletRequestAttributes();
        return getCallContext(sra.getRequest(), sra.getResponse());
    }

    public static CallContext getCallContext(HttpServletRequest request, HttpServletResponse response) {
        return new CallContext(getJEEContext(request, response), JEE_SESSION_STORE);
    }
}
