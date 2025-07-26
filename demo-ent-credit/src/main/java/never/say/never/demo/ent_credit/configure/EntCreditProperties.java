/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-13
 */
@ConfigurationProperties(prefix = "ent-credit")
@Component
@Getter
@Setter
public class EntCreditProperties {
    @NestedConfigurationProperty
    private Map<EntCreditBeanUnit, DataSourceProperties> datasource;
}
