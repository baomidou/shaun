/*
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
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

import com.baomidou.shaun.core.context.session.NoSessionStore;
import com.baomidou.shaun.core.exception.ShaunException;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author miemie
 * @since 2020-05-26
 */
public abstract class WebUtil {

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
    public static void redirectUrl(JEEContext context, String url) {
        redirectUrl(context, HttpConstants.FOUND, url);
    }

    /**
     * 重定向到指定页面
     *
     * @param context 上下文
     * @param code    httpCode
     * @param url     地址
     */
    public static void redirectUrl(JEEContext context, int code, String url) {
        final HttpServletResponse response = context.getNativeResponse();
        response.setHeader(HttpConstants.LOCATION_HEADER, url);
        response.setStatus(code);
    }

    /**
     * 写入内容
     *
     * @param context 上下文
     * @param content 信息
     */
    public static void write(JEEContext context, String content) {
        write(context, HttpConstants.OK, content);
    }

    /**
     * 写入内容
     *
     * @param context 上下文
     * @param code    httpCode
     * @param content 信息
     */
    public static void write(JEEContext context, int code, String content) {
        final HttpServletResponse response = context.getNativeResponse();
        response.setStatus(code);
        if (content != null) {
            try (OutputStream os = response.getOutputStream()) {
                os.write(content.getBytes(StandardCharsets.UTF_8));
                os.flush();
            } catch (IOException e) {
                throw new ShaunException(e);
            }
        }
    }

    public static JEEContext getJEEContext(boolean session) {
        ServletRequestAttributes sra = getServletRequestAttributes();
        return getJEEContext(sra.getRequest(), sra.getResponse(), session);
    }

    public static JEEContext getJEEContext(HttpServletRequest request, HttpServletResponse response, boolean session) {
        return new JEEContext(request, response, session ? JEESessionStore.INSTANCE : NoSessionStore.INSTANCE);
    }
}
