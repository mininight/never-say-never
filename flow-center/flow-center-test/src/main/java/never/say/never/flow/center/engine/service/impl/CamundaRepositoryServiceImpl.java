/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.flow.center.engine.service.impl;

import jakarta.annotation.Resource;
import never.say.never.flow.center.engine.dal.mapper.ActReDeploymentSourceMapper;
import never.say.never.flow.center.engine.service.CamundaRepositoryService;
import org.camunda.bpm.engine.impl.RepositoryServiceImpl;
import org.camunda.bpm.engine.impl.repository.DeploymentBuilderImpl;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * @author Ivan
 * @version 1.0.0
 * @date 2023-02-25
 */
public class CamundaRepositoryServiceImpl extends RepositoryServiceImpl implements CamundaRepositoryService {

    @Resource
    private ActReDeploymentSourceMapper actReDeploymentSourceMapper;

    @Resource
    private TransactionTemplate transactionTemplate;


    @Override
    public DeploymentWithDefinitions deployWithResult(DeploymentBuilderImpl deploymentBuilder) {
        DeploymentWithDefinitions deployResult = super.deployWithResult(deploymentBuilder);
        String deployId = deployResult.getId();
        return  deployResult;
    }
}
