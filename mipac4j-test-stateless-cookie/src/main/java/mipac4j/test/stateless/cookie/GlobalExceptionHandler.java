package mipac4j.test.stateless.cookie;

import org.pac4j.core.exception.HttpAction;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({HttpAction.class})
    public String bindException(HttpAction action) {
        if (action.getCode() == 403) {
            return "你没有权限";
        }
        return "你没登录";
    }
}