package com.baomidou.mipac4j.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author miemie
 * @since 2019-07-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HttpCodeException extends RuntimeException {

    private int code;

    public HttpCodeException(final int code) {
        super("Performing a " + code + " HTTP action");
        this.code = code;
    }
}
