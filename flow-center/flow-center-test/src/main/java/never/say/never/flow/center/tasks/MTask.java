/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.flow.center.tasks;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2023-02-25
 */
public class MTask implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariable("Magree", System.currentTimeMillis() % 2 == 0);
        System.out.println("@@@主管审批");
    }
}
