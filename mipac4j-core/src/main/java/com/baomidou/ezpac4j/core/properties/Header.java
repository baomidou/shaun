package com.baomidou.ezpac4j.core.properties;

import org.springframework.http.HttpHeaders;

import lombok.Data;

/**
 * @author miemie
 * @since 2019-07-20
 */
@Data
public class Header {

    /**
     * header 的 name
     */
    private String headerName = HttpHeaders.AUTHORIZATION;
    /**
     * headerName 的值的前缀
     */
    private String prefixHeader = "";
}
