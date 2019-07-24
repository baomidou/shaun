package com.baomidou.mipac4j.core.properties;

import org.pac4j.core.context.Pac4jConstants;

import lombok.Data;

/**
 * @author miemie
 * @since 2019-07-20
 */
@Data
public class Cookie {

    private String name = Pac4jConstants.SESSION_ID;
    private int version = 0;
    private String comment;
    private String domain = "";
    private String path = Pac4jConstants.DEFAULT_URL_VALUE;
    private boolean secure;
    private boolean isHttpOnly = false;
}
