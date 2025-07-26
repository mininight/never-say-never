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
import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.http.HttpApiClientPanel;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-14
 */
@Getter
public enum AiQiChaCompanyRelationType {

    /**
     * 公司最新公示的股东信息
     */
    companyShares1("公司最新公示的股东信息", CompanyStock.class),

    /**
     * 公司工商登记的股东信息
     */
    companyShares2("公司工商登记的股东信息", CompanyStock.class),

    /**
     * 公司主要人员信息
     */
    companyKeyPerson("公司主要人员信息", CompanyKeyPerson.class),

    /**
     * 企业对外投资企业
     */
    companyInvest("企业对外投资企业", CompanyInvest.class),

    /**
     * 企业控股企业
     */
    companyHolds("企业控股企业", CompanyHolds.class),

    /**
     * 企业间接持股企业
     */
    companyIndirectHolds("企业间接持股企业", CompanyIndirectHolds.class),

    /**
     * 公司变更记录
     */
    companyChangeRecord("公司变更记录", CompanyChangeRecord.class),
    ;
    private final String describe;
    private final Type entityClass;
    private final Method apiMethod;

    AiQiChaCompanyRelationType(String describe, Type entityClass) {
        this.describe = describe;
        this.entityClass = entityClass;
        apiMethod = ReflectionUtils.findMethod(AiQiChaHttpApiClient.class, this.name(), int.class, int.class, String.class);
        Preconditions.checkArgument(apiMethod != null, "未匹配到" +
                AiQiChaHttpApiClient.class.getName() + "#" + this.name());
        ReflectionUtils.makeAccessible(apiMethod);
    }

    public JSONObject touchApi(int page, int size, String compId) {
        return (JSONObject) ReflectionUtils.invokeMethod(apiMethod, HttpApiClientPanel.AiQiCha, page, size, compId);
    }
}
