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
package com.baomidou.shaun.core.credentials.location;

import lombok.Data;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.extractor.HeaderExtractor;

/**
 * {@link HeaderExtractor}
 *
 * @author miemie
 * @since 2019-07-20
 */
@Data
public class Header {

    /**
     * header 的 name
     */
    private String name = HttpConstants.AUTHORIZATION_HEADER;
    /**
     * headerName 的值的前缀
     */
    private String prefix = "";
    /**
     * 去除空串
     */
    private boolean trimValue;
}
