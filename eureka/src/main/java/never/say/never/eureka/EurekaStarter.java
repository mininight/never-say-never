/*
 *  Copyright (c) 2018-2022 the original author or authors.
 *  Author: 861828396@qq.com
 */

package never.say.never.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Hello world!
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaStarter {
    public static void main(String[] args) {
        SpringApplication.run(EurekaStarter.class, args);
    }
}
