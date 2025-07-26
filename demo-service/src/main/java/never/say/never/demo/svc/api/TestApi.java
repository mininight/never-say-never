/*
 *  Copyright (c) 2018-2022 the original author or authors.
 *  Author: 861828396@qq.com
 */

package never.say.never.demo.svc.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * TestApi
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2022-06-29
 */
@FeignClient(name = "testApi", path = "/testApi")
public interface TestApi {

    @PostMapping("/sayHello")
    Object sayHello();
}
