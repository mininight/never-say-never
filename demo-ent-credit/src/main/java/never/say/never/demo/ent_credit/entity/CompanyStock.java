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
import org.apache.commons.lang3.StringUtils;

/**
 * 公司股东
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyStock extends Person {
    private String compId;
    private String stockId;
    @JSONField(name = "name")
    private String stockName;
    @JSONField(name = "compNum")
    private String haveCompNum;
    private String subRate;
    private String subMoney;
    private String subDate;

    @Override
    public boolean O_K() {
        if (StringUtils.isBlank(getStockId()) && StringUtils.isNotBlank(getPersonId())) {
            setStockId(getPersonId());
        }
        return StringUtils.isNotBlank(getStockId());
    }
}
