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
package com.baomidou.shaun.autoconfigure.properties;

import com.baomidou.shaun.core.filter.ActuatorFilter;
import lombok.Data;

/**
 * @author miemie
 * @since 2022-06-06
 */
@Data
public class ActuatorProperties {

    /**
     * 是否启用对 actuator 地址的支持
     * 如果不启用则 {@link #basePath} 的值也会被安全拦截器拦截检查用户token状态(不建议)
     * 如果不启用,为了 actuator 的安全性建议参考 {@link ActuatorFilter} 实现一个 filter 注入到 spring 中
     */
    private boolean enable = true;
    /**
     * see management.endpoints.web.base-path
     */
    private String basePath = "/actuator";
    /**
     * see spring.boot.admin.client.username
     * <p>
     * https://learning.postman.com/docs/sending-requests/authorization/#basic-auth
     */
    private String username;
    /**
     * see spring.boot.admin.client.password
     */
    private String password;
}
