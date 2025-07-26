/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.flow.center.autoconfig;

import never.say.never.flow.center.engine.cfg.CustomizeCamundaConfiguration;
import never.say.never.flow.center.engine.idgen.DefaultIdGenerator;
import org.camunda.bpm.engine.impl.cfg.IdGenerator;
import org.camunda.bpm.spring.boot.starter.property.CamundaBpmProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.camunda.bpm.spring.boot.starter.configuration.id.IdGeneratorConfiguration.PROPERTY_NAME;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2023-02-25
 */
@MapperScan("never.say.never.flow.center.engine.dal")
@EnableTransactionManagement
@Configuration
public class FlowCenterConfiguration {

    public static final String ID_GEN_DEFAULT = "default";

    @Bean
    @ConditionalOnProperty(prefix = CamundaBpmProperties.PREFIX, name = PROPERTY_NAME, havingValue = ID_GEN_DEFAULT, matchIfMissing = true)
    public IdGenerator idGenerator(){
        return new DefaultIdGenerator();
    }

    @Bean
    public CustomizeCamundaConfiguration customizeCamundaConfiguration(IdGenerator idGenerator){
        return new CustomizeCamundaConfiguration(idGenerator);
    }
}
