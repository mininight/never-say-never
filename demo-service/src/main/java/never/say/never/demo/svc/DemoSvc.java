/*
 *  Copyright (c) 2018-2022 the original author or authors.
 *  Author: 861828396@qq.com
 */

package never.say.never.demo.svc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * DemoSvc
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2022-06-29
 */
@EnableDiscoveryClient
@SpringBootApplication
public class DemoSvc {

    public static void main(String[] args) {
        SpringApplication.run(DemoSvc.class, args);
    }
}
