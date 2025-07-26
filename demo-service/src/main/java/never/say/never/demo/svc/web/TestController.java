/*
 *  Copyright (c) 2018-2022 the original author or authors.
 *  Author: 861828396@qq.com
 */

package never.say.never.demo.svc.web;

import never.say.never.demo.svc.api.TestApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2022-06-29
 */
@RequestMapping("/testApi")
@RestController
public class TestController implements TestApi {

    @Override
    public Object sayHello() {
        return System.currentTimeMillis();
    }
}
