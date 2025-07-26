/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api.dto;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Sets;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-09
 */
@Data
public class QiChaChaCompany {
    private String _id;
    private String No;
    private String Name;
    private String BelongOrg;
    private String StartDate;
    private String Status;
    private String CreditCode;
    private String RegistCapi;
    private String RecCap;
    private String Address;
    private String Scope;
    private String OrgNo;
    private String TermStart;
    private String TeamEnd;
    private String CheckDate;
    private String TaxNo;
    private JSONObject Oper;
    private List<JSONObject> OriginalName;
    private String belongArea;
    private JSONObject Industry;
    private JSONObject IndustryV3;
    private JSONObject QccIndustry;
    private List<JSONObject> MoreEmailList;
    private JSONObject BankInfo;
    private String ShortName;
    private JSONObject ContactInfo;

    public String originalName() {
        if (CollectionUtils.isEmpty(OriginalName)) {
            return "";
        }
        return OriginalName.stream().map(n -> {
            String text;
            if (n.containsKey("text")) {
                text = n.getString("text");
            } else {
                text = n.getString("Name") + "(" + n.getString("DateLabel") + ")";
            }
            text = text.replaceAll("\n", "");
            text = text.replaceAll(" ", "");
            return text;
        }).collect(Collectors.joining("; "));
    }

    public String getStartDate() {
        if (StartDate == null) {
            return null;
        }
        if (StringUtils.isNumeric(StartDate)) {
            StartDate = DateFormatUtils.format(Long.parseLong(StartDate) * 1000, "yyyy-MM-dd");
        }
        return StartDate;
    }

    public String openTime() {
        if (TermStart == null) {
            return null;
        }
        if (StringUtils.isNumeric(TermStart)) {
            TermStart = DateFormatUtils.format(Long.parseLong(TermStart) * 1000, "yyyy-MM-dd");
        }
        if (StringUtils.isBlank(TeamEnd) || TeamEnd.length() < 2) {
            TeamEnd = null;
        }
        if (StringUtils.isNumeric(TeamEnd)) {
            TeamEnd = DateFormatUtils.format(Long.parseLong(TeamEnd) * 1000, "yyyy-MM-dd");
        }
        TeamEnd = StringUtils.isBlank(TeamEnd) ? "无固定期限" : TeamEnd;
        return TermStart + " 至 " + TeamEnd;
    }

    public String industry() {
        Industry = Industry == null ? new JSONObject() : Industry;
        IndustryV3 = IndustryV3 == null ? new JSONObject() : IndustryV3;
        QccIndustry = QccIndustry == null ? new JSONObject() : QccIndustry;
        Set<String> set = Sets.newHashSet(
                Industry.getString("Industry"),
                Industry.getString("SubIndustry"),
                IndustryV3.getString("Industry"),
                IndustryV3.getString("SubIndustry"),
                IndustryV3.getString("MiddleCategory"),
                IndustryV3.getString("SmallCategory"),
                QccIndustry.getString("An"),
                QccIndustry.getString("Bn"),
                QccIndustry.getString("Cn")
        );
        return String.join("; ", set);
    }

    public String email() {
        if (CollectionUtils.isEmpty(MoreEmailList)) {
            return "";
        }
        return MoreEmailList.stream().map(jsonObject -> jsonObject.getString("e"))
                .collect(Collectors.joining("; "));
    }

    public String bankInfo() {
        if (BankInfo == null || BankInfo.isEmpty()) {
            return "";
        }
        return String.join(":", BankInfo.getString("Bank"),
                BankInfo.getString("BankAccount"));
    }

    public Integer operCompNum() {
        if (Oper != null && Oper.isEmpty()) {
            return Oper.getInteger("CompanyCount");
        }
        return null;
    }
}
