/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.entity;

import lombok.Data;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-11
 */
@Data
public class PullLogSub {
    private String sourceKey;
    private String parentKey;
    private String channel;
    private String type;
    private Integer level;
}
