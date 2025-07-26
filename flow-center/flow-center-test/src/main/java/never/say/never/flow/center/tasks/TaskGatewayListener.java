/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.flow.center.tasks;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2023-02-25
 */
//@Component
public class TaskGatewayListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        System.out.println(JsonUtil.toString(execution.getVariables()));
        execution.setVariable("dayNum", System.currentTimeMillis() % 2 == 0 ? 1 : 2);
        System.out.println("@@@dayNum injected");
    }
}
