package never.say.never.test.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-31
 */
@RestController
public class TestController {

    @RequestMapping("/test")
    public Object test() {
        return new TestVO();
    }
}
