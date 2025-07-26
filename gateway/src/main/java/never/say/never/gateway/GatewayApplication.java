/*
 *  Copyright (c) 2018-2022 the original author or authors.
 *  Author: 861828396@qq.com
 */

package never.say.never.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2022-06-20
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
