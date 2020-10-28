package shaun.test.cookie;

import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({HttpAction.class})
    public String httpCodeException(Model model, HttpAction action) {
        String e = "未知异常";
        if (action instanceof UnauthorizedAction) {
            e = "请登录";
        } else if (action instanceof ForbiddenAction) {
            e = "你没有权限";
        }
        model.addAttribute("a", e);
        return "a";
    }
}