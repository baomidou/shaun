package com.baomidou.shaun.core.properties;

import org.pac4j.core.context.HttpConstants;

import lombok.Data;

/**
 * @author miemie
 * @since 2019-07-20
 */
@Data
public class Parameter {

    /**
     * parameter 的 name
     */
    private String parameterName = HttpConstants.AUTHORIZATION_HEADER;
    /**
     * 支持 get 请求
     */
    private boolean supportGetRequest = true;
    /**
     * 支持 post 请求
     */
    private boolean supportPostRequest = false;
}
