/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.enums;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Preconditions;
import lombok.Getter;
import never.say.never.demo.ent_credit.api.AiQiChaHttpApiClient;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-29
 */
@Getter
public enum AiQiChaPersonCompanyRelationType {

    /**
     * 担任法定代表人
     */
    personAsLegalEnterprises("担任法定代表人"),

    /**
     * 担任股东
     */
    personIsStockholderEnterprises("担任股东"),

    /**
     * 担任高管
     */
    personIsDirectorsEnterprises("担任高管"),

    /**
     * 任职过或正在任职
     */
    personAllEnterprises("任职过或正在任职"),

    /**
     * 最终受益人
     */
    personFinalBenefitEnterprises("最终受益人"),

    /**
     * 曾经担任法定代表人
     */
    personBeforeLegalPersonEnterprises("曾经担任法定代表人"),

    /**
     * 曾经担任股东
     */
    personBeforeStockholderEnterprises("曾经担任股东"),

    /**
     * 曾经担任高管
     */
    personBeforeDirectorsEnterprises("曾经担任高管"),

    /**
     * 曾经任职
     */
    personBeforeAllEnterprises("曾经任职"),

    /**
     * 曾经是最终受益人
     */
    personBeforeFinalBenefitEnterprises("曾经是最终受益人"),

    /**
     * 曾经控股
     */
    personBeforeHoldsEnterprises("曾经控股"),

    /**
     * 正在控股
     */
    personHoldsEnterprises("正在控股"),

    /**
     * 间接持股
     */
    personIndirectHoldsEnterprises("间接持股"),

    ;
    private final String describe;
    private final Method apiMethod;

    AiQiChaPersonCompanyRelationType(String describe) {
        this.describe = describe;
        apiMethod = ReflectionUtils.findMethod(AiQiChaHttpApiClient.class, this.name(), int.class, int.class, String.class);
        Preconditions.checkArgument(apiMethod != null, "未匹配到" +
                AiQiChaHttpApiClient.class.getName() + "#" + this.name());
        ReflectionUtils.makeAccessible(apiMethod);
    }

    public JSONObject touchApi(AiQiChaHttpApiClient aiQiChaHttpApiClient, int page, int size, String personId) {
        return (JSONObject) ReflectionUtils.invokeMethod(apiMethod, aiQiChaHttpApiClient, page, size, personId);
    }

}
