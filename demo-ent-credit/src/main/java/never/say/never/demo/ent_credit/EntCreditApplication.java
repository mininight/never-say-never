/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Hello world!
 *
 * @author Ivan
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EntCreditApplication {
    public static void main(String[] args) {
        SpringApplication.run(EntCreditApplication.class, args);
    }
}
