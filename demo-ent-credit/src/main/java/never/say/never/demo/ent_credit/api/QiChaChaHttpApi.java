/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import com.alibaba.fastjson2.JSONObject;
import never.say.never.demo.ent_credit.api.dto.QiChaChaCompany;
import never.say.never.demo.ent_credit.api.dto.QiChaChaCompany2;
import never.say.never.demo.ent_credit.entity.Company;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-07
 */
public interface QiChaChaHttpApi extends HttpEntCreditApi {

    @Override
    default JSONObject checkJsonResult(ExecSupplier<JSONObject> supplier) throws Throwable {
        JSONObject apiResult = supplier.get();
        apiResult = apiResult == null ? new JSONObject() : apiResult;
        if (apiResult.getIntValue("status") != 200) {
            throw new IllegalAccessException(String.format("接口无法访问，apiResult:%s", apiResult.toJSONString()));
        }
        return apiResult;
    }

    @Override
    default void applyCompanyJson(Company compParam, JSONObject companyJson) {
        if (companyJson == null || companyJson.isEmpty()) {
            return;
        }
        if (companyJson.containsKey("DJInfo")) {
            QiChaChaCompany2 qiChaChaCompany2 = companyJson.toJavaObject(QiChaChaCompany2.class);
            QiChaChaCompany2.DJInfo djInfo = qiChaChaCompany2.getDJInfo();
            compParam.setLevel(HttpApiRequestContext.getCurrent().getLevel());
            compParam.setEntLogoWord(qiChaChaCompany2.getShortName());
            compParam.setLegalCompNum(djInfo.getOper().getInteger("CompanyCount"));
            compParam.setOpenStatus(djInfo.getStatus());
            compParam.setUnifiedCode(djInfo.getCreditCode());
            compParam.setRegAddr(djInfo.getAddress());
            compParam.setDistrict(djInfo.getAddress());
            compParam.setScope(djInfo.getScope());
            compParam.setStartDate(djInfo.getStartDate());
            compParam.setOpenTime(djInfo.getCertificatePeriod());
            compParam.setAuthority(djInfo.getFazhengAuthority());
            compParam.setRegCapital(djInfo.getRegistCapi());
            compParam.setIndustry(djInfo.getEconKind());
            compParam.setEmail(qiChaChaCompany2.getEmail());
            compParam.setTelephone(qiChaChaCompany2.getContactNo());
        } else {
            QiChaChaCompany qiChaChaCompany = companyJson.toJavaObject(QiChaChaCompany.class);
            compParam.setLevel(HttpApiRequestContext.getCurrent().getLevel());
            compParam.setPreEntName(qiChaChaCompany.originalName());
            compParam.setLegalCompNum(qiChaChaCompany.operCompNum());
            compParam.setOpenStatus(qiChaChaCompany.getStatus());
            compParam.setUnifiedCode(qiChaChaCompany.getCreditCode());
            compParam.setTaxNo(qiChaChaCompany.getTaxNo());
            compParam.setOrgNo(qiChaChaCompany.getOrgNo());
            compParam.setRegCode(qiChaChaCompany.getNo());
            compParam.setRegNo(qiChaChaCompany.getNo());
            compParam.setRegAddr(qiChaChaCompany.getAddress());
            compParam.setDistrict(qiChaChaCompany.getBelongArea());
            compParam.setScope(qiChaChaCompany.getScope());
            compParam.setStartDate(qiChaChaCompany.getStartDate());
            compParam.setOpenTime(qiChaChaCompany.openTime());
            compParam.setAnnualDate(qiChaChaCompany.getCheckDate());
            compParam.setRegCapital(qiChaChaCompany.getRegistCapi());
            compParam.setRealCapital(qiChaChaCompany.getRecCap());
            compParam.setIndustry(qiChaChaCompany.industry());
            compParam.setEmail(qiChaChaCompany.email());
            compParam.setTelephone(qiChaChaCompany.getContactInfo().getString("PhoneNumber"));
            compParam.setAuthority(qiChaChaCompany.getBelongOrg());
            compParam.setBankInfo(qiChaChaCompany.bankInfo());
        }
    }
}
