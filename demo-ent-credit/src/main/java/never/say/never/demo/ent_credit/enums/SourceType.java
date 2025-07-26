/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.enums;

import org.apache.commons.lang3.EnumUtils;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-24
 */
public enum SourceType {
    /**
     * 公司
     */
    company,

    /**
     * 人员
     */
    person,

    /**
     * 集团
     */
    EntGroup,
    ;

    public static SourceType of(String name) {
        return EnumUtils.getEnum(SourceType.class, name);
    }
}
