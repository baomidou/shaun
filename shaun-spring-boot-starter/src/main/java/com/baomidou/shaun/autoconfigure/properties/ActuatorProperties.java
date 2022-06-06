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

import lombok.Data;

/**
 * @author miemie
 * @since 2022-06-06
 */
@Data
public class ActuatorProperties {
    /**
     * see management.endpoints.web.base-path
     */
    private String branch = "/actuator";
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
