/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-09
 */
@Data
public class TianYanChaCompany {
    private String id;
    private String name;
    private String alias;
    private String regStatus;
    private String companyCreditCode;
    private String orgNumber;
    private String taxNumber;
    private String regNumber;
    private String regLocation;
    private String regCapital;
    private Map<String, String> industryInfo;
    private String regInstitute;
    private List<String> emailList;
    private String phoneNumber;
    private JSONObject legalInfo;
    private String businessScope;
    private String registrationDate;
    private Long approvedTime;
    private String expiryDate;

    public Integer legalCompanyNum() {
        return legalInfo == null ? null : legalInfo.getInteger("companyNum");
    }

    public String email() {
        if (CollectionUtils.isEmpty(emailList)) {
            return "";
        }
        return String.join("; ", emailList);
    }

    public String industryInfo() {
        if (industryInfo == null || industryInfo.isEmpty()) {
            return "";
        }
        industryInfo.values().removeIf(StringUtils::isBlank);
        return String.join("; ", industryInfo.values());
    }

    public String registrationDate() {
        if (registrationDate == null) {
            return "";
        }
        return registrationDate;
    }

    public String approvedTime() {
        if (approvedTime == null) {
            return "";
        }
        return DateFormatUtils.format(approvedTime, "yyyy-MM-dd");
    }
}
