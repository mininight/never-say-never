/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.flow.center.engine.idgen;

import org.camunda.bpm.engine.impl.cfg.IdGenerator;
import org.camunda.bpm.engine.impl.persistence.StrongUuidGenerator;

import java.util.UUID;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2023-03-07
 */
public class DefaultIdGenerator extends StrongUuidGenerator implements IdGenerator {

    @Override
    public String getNextId() {
        UUID uuid = timeBasedGenerator.generate();
        return uuid.toString().replaceAll("-", "");
    }
}
