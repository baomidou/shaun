package shaun.test.actuator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author miemie
 * @since 2019-08-04
 */
@RestController
@Controller
public class TestController {

    @GetMapping("/index")
    public String login() {
        return "index";
    }
}
