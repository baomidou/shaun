package shaun.test.cookie;

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
        switch (action.getCode()) {
            case 302:
                return "";
            case 401:
                return "请登录";
            case 403:
                return "你没有权限";
        }
        return "";
    }
}