package com.baomidou.mipac4j.core.properties;

import org.springframework.http.HttpHeaders;

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
    private String parameterName = HttpHeaders.AUTHORIZATION;
    /**
     * 支持 get 请求
     */
    private boolean supportGetRequest = true;
    /**
     * 支持 post 请求
     */
    private boolean supportPostRequest = false;
}
