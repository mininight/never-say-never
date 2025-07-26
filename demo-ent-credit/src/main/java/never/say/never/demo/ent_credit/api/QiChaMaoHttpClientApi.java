/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Preconditions;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import never.say.never.demo.ent_credit.api.dto.QiChaMaoCompany;
import never.say.never.demo.ent_credit.entity.Company;
import never.say.never.demo.ent_credit.http.HeaderTemplate;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.http.RefreshingCookie;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-11
 */
public interface QiChaMaoHttpClientApi extends HttpEntCreditApi {

    String BASE_HOST = "www.qichamao.com";
    String BASE_URL = "https://" + BASE_HOST;

    @Override
    default void prepareHeaders(HttpApiRequestContext context) {
        context.getCustomHeaders().putAll(HeaderTemplate.QiChaMao_header.getKvMap());
        context.updateHeader("Host", BASE_HOST);
        context.updateHeader("Origin", BASE_URL);
        context.updateHeader("Cookie", RefreshingCookie.QiChaMao_cookie.val());
    }

    @RequestLine("POST /orgcompany/GetOrgCompanyTips")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8")
    JSONObject suggest(@QueryMap JSONObject query);

    @RequestLine("GET /orgcompany/searchitemdtl/{pid}.html")
    @feign.Headers("Content-Type: text/html")
    String compInfo(@Param("pid") String pid);

    @Override
    default Company grabEntCreditBasic(Company compParam) throws Throwable {
        if (compParam == null || StringUtils.isBlank(compParam.getEntName())) {
            return compParam;
        }
        String companyName = compParam.getEntName();
        JSONObject query = new JSONObject();
        query.put("prefixList", "");
        query.put("tip", companyName);
        query.put("limit", 6);
        query.put("mark", 0.7882428913374311);
        JSONObject apiResult = suggest(query);
        Preconditions.checkArgument(apiResult != null && apiResult.getBoolean("succ"),
                "接口请求失败");
        String pid = apiResult.getJSONArray("lst").toJavaList(JSONObject.class).stream()
                .filter(item -> companyName.equals(item.getString("companyName")))
                .map(item -> item.getString("companyCode"))
                .findFirst().orElse(null);
        String compPage = compInfo(pid);
        Document compDoc = Jsoup.parse(compPage);
        Elements elements = compDoc.select("#companyDetailBox div.qd-table-body ul li");
        JSONObject companyJson = new JSONObject();
        for (Element element : elements) {
            Element tagName = element.selectFirst("span");
            Element tagValue = element.selectFirst("div");
            String key = tagName == null ? null : tagName.text();
            if (StringUtils.isBlank(key)) {
                continue;
            }
            key = key.replaceAll(":", "");
            key = key.replaceAll("：", "").trim();
            String value = tagValue == null ? null : tagValue.text().trim();
            companyJson.put(key, value);
        }
        applyCompanyJson(compParam, companyJson);
        return compParam;
    }

    @Override
    default void applyCompanyJson(Company compParam, JSONObject companyJson) {
        if (companyJson == null || companyJson.isEmpty()) {
            return;
        }
        QiChaMaoCompany qiChaMaoCompany = companyJson.toJavaObject(QiChaMaoCompany.class);
        compParam.setOpenStatus(qiChaMaoCompany.get经营状态());
        compParam.setPreEntName(qiChaMaoCompany.get曾用名());
        compParam.setEntLogoWord(qiChaMaoCompany.get商标名查询());
        compParam.setUnifiedCode(qiChaMaoCompany.get统一社会信用代码());
        compParam.setTaxNo(qiChaMaoCompany.get纳税人识别号());
        compParam.setRegNo(qiChaMaoCompany.get注册号());
        compParam.setOrgNo(qiChaMaoCompany.get机构代码());
        compParam.setRegAddr(qiChaMaoCompany.get企业地址());
        compParam.setDistrict(compParam.getRegAddr());
        compParam.setScope(qiChaMaoCompany.get经营范围());
        compParam.setStartDate(qiChaMaoCompany.get成立日期());
        compParam.setOpenTime(qiChaMaoCompany.get经营期限());
        compParam.setRegCapital(qiChaMaoCompany.get注册资本());
        compParam.setAuthority(qiChaMaoCompany.get登记机关());
        compParam.setAnnualDate(qiChaMaoCompany.get核准日期());
        compParam.setIndustry(qiChaMaoCompany.industry());
    }
}
