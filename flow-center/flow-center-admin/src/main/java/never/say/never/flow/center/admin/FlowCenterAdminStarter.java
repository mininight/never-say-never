/*
 *  Copyright (c) 2018-2022 the original author or authors.
 *  Author: 861828396@qq.com
 */

package never.say.never.flow.center.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * FlowCenterAdminStarter
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2022-12-05
 */
@EnableTransactionManagement
@SpringBootApplication
public class FlowCenterAdminStarter {
    public static void main(String[] args) {
        SpringApplication.run(FlowCenterAdminStarter.class, args);
    }
}
