package mipac4j.test.stateless.header;

import org.pac4j.core.exception.HttpAction;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({HttpAction.class})
    public String httpCodeException(HttpAction action) {
        return "你没有权限";
    }
}