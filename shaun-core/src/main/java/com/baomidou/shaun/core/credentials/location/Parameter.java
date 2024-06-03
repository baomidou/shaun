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
package com.baomidou.shaun.core.credentials.location;

import lombok.Data;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.extractor.ParameterExtractor;

/**
 * {@link ParameterExtractor}
 *
 * @author miemie
 * @since 2019-07-20
 */
@Data
public class Parameter {

    /**
     * parameter 的 name
     */
    private String name = HttpConstants.AUTHORIZATION_HEADER;
    /**
     * 支持 get 请求
     */
    private boolean supportGetRequest = true;
    /**
     * 支持 post 请求
     */
    private boolean supportPostRequest = true;
}
