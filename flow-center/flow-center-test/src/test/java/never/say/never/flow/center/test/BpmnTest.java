package never.say.never.flow.center.test;

import never.say.never.flow.center.FlowCenterTestStarter;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.impl.test.AbstractProcessEngineTestCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-01
 */
@Rollback(false)
@SpringBootTest(classes = FlowCenterTestStarter.class)
public class BpmnTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    ProcessEngine processEngine;

    @Test
    public void test(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ManagementService managementService = processEngine.getManagementService();
        TaskService taskService = processEngine.getTaskService();
        ExternalTaskService externalTaskService = processEngine.getExternalTaskService();
        FilterService filterService = processEngine.getFilterService();
        System.out.println();
    }
}
