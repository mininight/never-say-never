/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.logger;

import never.say.never.demo.ent_credit.entity.ExecFailed;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-07
 */
public interface ExecFailedLoggerRepository {
    void logExecFailed(ExecFailed execFailed);
}
