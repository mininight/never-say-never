/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyKeyPerson extends Person {
    private String compId;
    @JSONField(name = "compNum")
    private String haveCompNum;
    private String positionTitle;
}
