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
