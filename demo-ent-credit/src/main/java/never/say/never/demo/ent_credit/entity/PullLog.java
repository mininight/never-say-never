/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-11
 */
@Data
public class PullLog {
    private String sourceKey;
    private String channel;
    private String type;
    private Boolean finished;
    private Integer level;
    private LocalDateTime begin_time;
    private LocalDateTime end_time;
}
