/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.flow.center.engine.cfg;

import never.say.never.flow.center.engine.service.impl.CamundaRepositoryServiceImpl;
import org.camunda.bpm.engine.impl.cfg.IdGenerator;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.configuration.impl.AbstractCamundaConfiguration;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2023-02-25
 */
public class CustomizeCamundaConfiguration extends AbstractCamundaConfiguration {

    private final IdGenerator idGenerator;

    public CustomizeCamundaConfiguration(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public void preInit(SpringProcessEngineConfiguration processEngineConfiguration) {
        super.preInit(processEngineConfiguration);
        //
        processEngineConfiguration.setIdGenerator(idGenerator);
        processEngineConfiguration.setRepositoryService(new CamundaRepositoryServiceImpl());
    }
}
