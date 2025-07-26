/*
 *  Copyright (c) 2018-2022 the original author or authors.
 *  Author: 861828396@qq.com
 */

package never.say.never.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Hello world!
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigServer
public class ConfigStarter {
    public static void main(String[] args) {
        SpringApplication.run(ConfigStarter.class, args);
    }
}
