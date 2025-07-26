/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.configure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static never.say.never.demo.ent_credit.configure.EntCreditBeanUnit.*;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-12
 */
@Configuration
@EnableConfigurationProperties
public class RootConfiguration {

    @Bean
    EntCreditBeanUnit belowEntCreditBeanUnit() {
        return BELOW;
    }

    @Bean
    EntCreditBeanUnit topEntCreditBeanUnit() {
        return TOP;
    }

    @Bean
    EntCreditBeanUnit EntCreditBeanUnit_AQC_BACKUP_0805() {
        return AQC_BACKUP_0805;
    }

    @Bean
    EntCreditBeanUnit EntCreditBeanUnit_AQC_BACKUP_0818() {
        return AQC_BACKUP_0818;
    }
}
