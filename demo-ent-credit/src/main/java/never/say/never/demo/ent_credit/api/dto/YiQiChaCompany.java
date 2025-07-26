/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api.dto;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.stream.Collectors;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-11
 */
@Data
public class YiQiChaCompany {
    private String regNo;
    private JSONObject relateEntStatInfo;
    private String opScope;
    private String dom;
    private String districtCode;
    private String entName;
    private String year;
    private String cityCode;
    private JSONArray historyName;
    private String esDateDesc;
    private String pid;
    private Long lon;
    private String staffNum;
    private String regOrgDesc;
    private Long entType;
    private String enterpriseSize;
    private String importExportCode;
    private String industryFirstCode;
    private String regCapCur;
    private String entStatusDesc;
    private String staffSize;
    private String cityName;
    private String orgCode;
    private String legalPerson;
    private String apprDateDesc;
    private String regCap;
    private String opToDesc;
    private String introduction;
    private Long lat;
    private String participants;
    private JSONArray industryName;
    private String uncid;
    private String address;
    private String districtName;
    private String provinceCode;
    private String recCap;
    private String entNameEn;
    private String apprDate;
    private String url;
    private String qualification;
    private String insureCount;
    private String opFromDesc;
    private String entTypeDesc;
    private Long entStatus;
    private String provinceName;


    public String historyName() {
        if (CollectionUtils.isEmpty(historyName)) {
            return "";
        }
        return historyName.toJavaList(JSONObject.class).stream()
                .map(item -> String.join("", item.getString("historyName"),
                        "(", item.getString("alterDateDesc"), "至", item.getString("endDateDesc"), ")")
                ).collect(Collectors.joining(";"));

    }

    public String industry() {
        if (CollectionUtils.isEmpty(industryName)) {
            return "";
        }
        return String.join("; ", industryName.toJavaList(String.class));
    }
}
