/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.flow.center.test;

import jakarta.annotation.Resource;
import never.say.never.flow.center.engine.service.CamundaRepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.io.File;
import java.util.List;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2023-02-24
 */
@SpringBootTest
@Rollback(false)
public class TestForm extends AbstractTransactionalJUnit4SpringContextTests {

    @Resource
    CamundaRepositoryService repositoryService;


    @Test
    public void createOrUpdateDeployment() {
        File file = new File("C:\\Users\\xulia\\Desktop\\请假.bpmn");
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        Deployment deployment = deploymentBuilder
                .addDeploymentResourceById("552eef24bd9111ed81c4502b7308632f","")
                .deploy();
//        String deployId = deployment.getId();
//        System.out.println(deployId);
//        InputStream modelXmlResource = repositoryService.getResourceAsStream("fdce8538-b4af-11ed-ae58-502b7308632f",
//                "请假.bpmn");
//        DomDocument xmlDoc = Bpmn.readModelFromStream(modelXmlResource).getDocument();
//        System.out.println(xmlDoc.getDomSource().getNode());

    }

    @Test
    public void listDeployments() {
        List<Deployment> deployments = repositoryService.createDeploymentQuery().deploymentName("请假测试").list();
        System.out.println(deployments);
    }
}
