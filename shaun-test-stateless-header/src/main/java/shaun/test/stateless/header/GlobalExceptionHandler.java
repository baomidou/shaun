package shaun.test.stateless.header;

import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({HttpAction.class})
    public String httpCodeException(HttpAction action) {
        if (action instanceof UnauthorizedAction) {
            return "请登录";
        } else if (action instanceof ForbiddenAction) {
            return "你没有权限";
        }
        return "未知异常";
    }
}