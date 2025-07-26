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
 * @date 2024-07-29
 */
@Data
public class CompanyChangeRecord {
    private String compId;
    private String date;
    private String fieldName;
    private String oldValue;
    private String newValue;
}
