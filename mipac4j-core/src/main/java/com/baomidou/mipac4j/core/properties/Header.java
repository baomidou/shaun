package com.baomidou.mipac4j.core.properties;

import lombok.Data;
import org.springframework.http.HttpHeaders;

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
    /**
     * 去除空串
     */
    private boolean trimValue;
}
