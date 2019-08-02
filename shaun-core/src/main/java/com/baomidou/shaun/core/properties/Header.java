package com.baomidou.shaun.core.properties;

import org.pac4j.core.context.HttpConstants;

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
