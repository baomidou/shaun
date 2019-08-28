package com.baomidou.shaun.core.exception;

/**
 * @author miemie
 * @since 2019-08-28
 */
public class ShaunException extends RuntimeException {

    public ShaunException() {
    }

    public ShaunException(String message) {
        super(message);
    }

    public ShaunException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShaunException(Throwable cause) {
        super(cause);
    }

    public ShaunException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
