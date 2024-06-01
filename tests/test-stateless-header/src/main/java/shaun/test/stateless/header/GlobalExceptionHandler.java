package shaun.test.stateless.header;


import jakarta.servlet.http.HttpServletResponse;
import org.pac4j.core.context.HttpConstants;
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
    public String httpCodeException(HttpServletResponse response, HttpAction action) {
        if (action instanceof UnauthorizedAction) {
            response.setStatus(HttpConstants.UNAUTHORIZED);
            return "请登录";
        } else if (action instanceof ForbiddenAction) {
            response.setStatus(HttpConstants.FORBIDDEN);
            return "你没有权限";
        }
        return "未知异常";
    }
}