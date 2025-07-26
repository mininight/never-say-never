/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Preconditions;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import feign.RequestTemplate;
import never.say.never.demo.ent_credit.entity.Company;
import never.say.never.demo.ent_credit.entity.Person;
import never.say.never.demo.ent_credit.http.HeaderTemplate;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.http.RefreshingCookie;
import never.say.never.demo.ent_credit.util.JacksonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-28
 */
public interface AiQiChaHttpApiClient extends AiQiChaHttpApiClient_Backup {

    String BASE_HOST = "aiqicha.baidu.com";
    String BASE_URL = "https://" + BASE_HOST;

    // 个人风险 https://aiqicha.baidu.com/personrisk/personSelfIntelAjax?personId=e9dbd83687e6389c2fbb8348b6db49cc&page=1&dataType=all&riskLevel=all&objectType=all&size=10&intelType=selfIntel&fr=&changeDate=all
    // 个人风险 https://aiqicha.baidu.com/personrisk/personSelfIntelAjax?personId=e9dbd83687e6389c2fbb8348b6db49cc&page=1&dataType=all&riskLevel=all&objectType=all&size=10&intelType=unionIntel&fr=&changeDate=all
    // 合伙人 https://aiqicha.baidu.com/person/partnerListAjax?personId=e9dbd83687e6389c2fbb8348b6db49cc

    /**
     * 担任法定代表人
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /c/legalpersonAjax?p={page}&size={size}&personId={personId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personAsLegalEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 担任股东
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /c/isstockholderAjax?p={page}&size={size}&personId={personId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personIsStockholderEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 担任高管
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /c/isdirectorsAjax?p={page}&size={size}&personId={personId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personIsDirectorsEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 所有任职
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /c/allenterprisesAjax?p={page}&size={size}&personId={personId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personAllEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 最终受益人
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /c/personFinalBenefitAjax?p={page}&size={size}&personId={personId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personFinalBenefitEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 曾担任法定代表人
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /c/beforelegalpersonAjax?p={page}&size={size}&personId={personId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personBeforeLegalPersonEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 曾担任股东
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /c/beforeisstockholderAjax?p={page}&size={size}&personId={personId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personBeforeStockholderEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 曾担任高管
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /c/beforeisdirectorsAjax?p={page}&size={size}&personId={personId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personBeforeDirectorsEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 曾任职企业
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /c/beforeallenterprisesAjax?p={page}&size={size}&personId={personId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personBeforeAllEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 曾经作为最终受益人
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /c/beforefinalbenefitAjax?p={page}&size={size}&personId={personId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personBeforeFinalBenefitEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 历史控股企业
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /c/beforeholdsenterpriseAjax?p={page}&size={size}&personId={personId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personBeforeHoldsEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 控股企业
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /person/personHoldsAjax?personId={personId}&p={page}&size={size}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personHoldsEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 间接持股企业
     *
     * @param page
     * @param size
     * @param personId
     * @return
     */
    @RequestLine("GET /person/personIndirectHoldsAjax?personId={personId}&p={page}&size={size}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personIndirectHoldsEnterprises(@Param("page") int page, @Param("size") int size, @Param("personId") String personId);

    /**
     * 模糊匹配企业
     *
     * @return
     */
    @RequestLine("POST /index/suggest")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject suggest(@Param("q") String searchText);

    /**
     * 查公司ID
     *
     * @param companyName
     * @return
     */
    default String findCompanyId(String companyName) {
        JSONObject apiResult = suggest(companyName);
        JSONArray queryList = apiResult.getJSONObject("data").getJSONArray("queryList");
        if (queryList == null) {
            return null;
        }
        JSONObject item;
        String pid = null;
        String name = null;
        for (int i = 0; i < queryList.size(); i++) {
            item = queryList.getJSONObject(i);
            pid = item.getString("pid");
            name = item.getString("resultStr");
            name = name.replaceAll("<em>", "");
            name = name.replaceAll("</em>", "");
            if (companyName.equals(name)) {
                break;
            }
        }
        return pid;
    }

    /**
     * 公司最新公示的股东信息
     *
     * @param page
     * @param size
     * @param compId
     * @return
     */
    @RequestLine("GET /detail/sharesAjax?p={page}&size={size}&pid={compId}&type=3")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject companyShares1(@Param("page") int page, @Param("size") int size, @Param("compId") String compId);

    /**
     * 公司工商登记的股东信息
     *
     * @param page
     * @param size
     * @param compId
     * @return
     */
    @RequestLine("GET /detail/sharesAjax?p={page}&size={size}&pid={compId}&type=4")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject companyShares2(@Param("page") int page, @Param("size") int size, @Param("compId") String compId);

    /**
     * 公司主要人员信息
     *
     * @param page
     * @param size
     * @param compId
     * @return
     */
    @RequestLine("GET /detail/getDirectorsAjax?p={page}&size={size}&pid={compId}&type=3")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject companyKeyPerson(@Param("page") int page, @Param("size") int size, @Param("compId") String compId);

    /**
     * 企业对外投资企业
     *
     * @param page
     * @param size
     * @param compId
     * @return
     */
    @RequestLine("GET /detail/investajax?p={page}&size={size}&pid={compId}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject companyInvest(@Param("page") int page, @Param("size") int size, @Param("compId") String compId);

    /**
     * 企业控股企业
     *
     * @param page
     * @param size
     * @param compId
     * @return
     */
    @RequestLine("GET /detail/holdsAjax?pid={compId}&p={page}&size={size}&confirm=")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject companyHolds(@Param("page") int page, @Param("size") int size, @Param("compId") String compId);

    /**
     * 企业间接持股企业
     *
     * @param page
     * @param size
     * @param compId
     * @return
     */
    @RequestLine("GET /detail/entIndirectHoldsAjax?pid={compId}&p={page}&size={size}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject companyIndirectHolds(@Param("page") int page, @Param("size") int size, @Param("compId") String compId);

    /**
     * 公司变更记录
     *
     * @param page
     * @param size
     * @param compId
     * @return
     */
    @RequestLine("GET /c/changeRecordAjax?pid={compId}&p={page}&size={size}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject companyChangeRecord(@Param("page") int page, @Param("size") int size, @Param("compId") String compId);

    /**
     * 公司简要信息
     *
     * @return
     */
    @RequestLine("GET /s/advanceSearchAjax?q={q}&p=1&s=10&f=%7B%22searchtype%22:[%221%22]%7D&o=0")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject companySimpleInfo(@Param("q") String companyName);

    /**
     * 公司简要信息
     *
     * @return
     */
    @RequestLine("GET /icpsearch/sAjax?page=1&size=10&q={q}&f=%7B%22searchtype%22:[%22116%22]%7D")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject companySimpleInfo2(@Param("q") String companyName);

    /**
     * 公司信息
     *
     * @return
     */
    @RequestLine("POST /compdata/headinfoAjax")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject companyDetail(@QueryMap JSONObject query);

    /**
     * 公司信息页面
     *
     * @return
     */
    @RequestLine("GET /company_detail_{compId}")
    @feign.Headers("Content-Type: text/html")
    String companyDetailHtml(@Param("compId") String compId);

    /**
     * 公司风险信息页面
     *
     * @return
     */
    @RequestLine("GET /riskv2/riskindex?pid={compId}")
    @feign.Headers("Content-Type: text/html")
    String companyRiskDetailHtml(@Param("compId") String compId);

    /**
     * 人员简要信息
     *
     * @param personId
     * @return
     */
    @RequestLine("POST /person/personfloatInfoajax")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject personSimpleInfo(@Param("personId") String personId);

    /**
     * 人员信息页面
     *
     * @return
     */
    @RequestLine("GET /person?personId={personId}&subtab=personal-allenterprises")
    @feign.Headers("Content-Type: text/html")
    String personDetailHtml(@Param("personId") String personId);

    /**
     * 人员风险信息页面
     *
     * @return
     */
    @RequestLine("GET /personrisk?personId={personId}")
    @feign.Headers("Content-Type: text/html")
    String personRiskDetailHtml(@Param("personId") String personId);


    default void updateReferer(String value) {
        HttpApiRequestContext.getCurrent().updateHeader("Referer", value);
        HttpApiRequestContext.getCurrent().updateHeader("Zx-Open-Url", value);
    }

    @Override
    default void prepareHeaders(HttpApiRequestContext context) {
        RequestTemplate request = context.getRequest();
        String path = request.url();
        if (isHtmlRequest(request)) {
            context.getCustomHeaders().clear();
            context.getCustomHeaders().putAll(HeaderTemplate.AiQiCha_Html_header.getKvMap());
        } else {
            context.getCustomHeaders().clear();
            context.getCustomHeaders().putAll(HeaderTemplate.AiQiCha_header.getKvMap());
        }
        context.updateHeader("Host", BASE_HOST);
        context.updateHeader("cookie", RefreshingCookie.AiQiCha_cookie.val());
        updateReferer(BASE_URL);
    }

    @Override
    default void applyCompanyJson(Company compParam, JSONObject companyJson) {
        if (companyJson == null || companyJson.isEmpty()) {
            return;
        }
        Company companyInfo = companyJson.toJavaObject(Company.class);
        compParam.merge(companyInfo);
        Person legalPerson = new Person();
        legalPerson.setPersonId(compParam.getPersonId());
        legalPerson.setPersonName(compParam.getLegalPerson());
        compParam.setLegalPersonInfo(legalPerson);
        compParam.setLegalCompNum(companyJson.getInteger("compNum"));
        compParam.setDistrict(compParam.getRegAddr());
        String telephone = compParam.getTelephone();
        telephone = telephone == null || telephone.length() < 8 ? "" : telephone;
        compParam.setTelephone(telephone);
        JSONArray phoneinfo = companyJson.getJSONArray("phoneinfo");
        if (CollectionUtils.isNotEmpty(phoneinfo)) {
            compParam.setTelephone(phoneinfo.toJavaList(JSONObject.class).stream()
                    .map(obj -> obj.getString("phone"))
                    .collect(Collectors.joining("; ")));
        }
        JSONArray emailinfo = companyJson.getJSONArray("emailinfo");
        if (CollectionUtils.isNotEmpty(phoneinfo)) {
            compParam.setEmail(emailinfo.toJavaList(JSONObject.class).stream()
                    .map(obj -> obj.getString("email"))
                    .collect(Collectors.joining("; ")));
        }
    }

    default Company grabCompanySimpleInfo(Company company, boolean advance) throws Exception {
        if (company == null || StringUtils.isBlank(company.getEntName())) {
            return company;
        }
        String companyName = company.getEntName();
        JSONObject apiResult = advance ? companySimpleInfo(companyName) : companySimpleInfo2(companyName);
        Preconditions.checkArgument(apiResult != null,
                "未获取到公司简要信息，apiResult:{}");
        Preconditions.checkArgument(apiResult.getJSONObject("data") != null,
                "未获取到公司简要信息，apiResult:{}");
        JSONArray resultList = apiResult.getJSONObject("data").getJSONArray("resultList");
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(resultList),
                "未获取到公司简要信息，apiResult:%s", apiResult);
        JSONObject info = null;
        String pid;
        for (int i = 0; i < resultList.size(); i++) {
            info = resultList.getJSONObject(i);
            String entName = info.getString("entName");
            if (entName == null) {
                continue;
            }
            entName = entName.replaceAll("<em>", "");
            entName = entName.replaceAll("</em>", "");
            if (Objects.equals(companyName, entName)) {
                pid = info.getString("pid");
                company.setCompId(pid);
                break;
            }
        }
        if (info == null) {
            return null;
        }
        String personId = info.getString("personId");
        String personName = info.getString("legalPerson");
        Person person = new Person();
        person.setPersonId(personId);
        person.setPersonName(personName);
        Preconditions.checkArgument(person.basicValid(), "未从公司简要信息中获得法人信息");
        company.setPersonTitle("法定代表人");
        company.setPersonId(personId);
        company.setLegalPerson(personName);
        company.setLegalPersonInfo(person);
        return company;
    }

//    @Override
//    default Company grabEntCreditBasic(Company compParam) throws Throwable {
//        if (compParam == null || StringUtils.isBlank(compParam.getCompId())) {
//            return compParam;
//        }
//        String companyId = compParam.getCompId();
//        JSONObject query = new JSONObject();
//        query.put("pid", companyId);
//        JSONObject apiResult = companyDetail(query);
//        applyCompanyJson(compParam, apiResult.getJSONObject("data"));
//        return compParam;
//    }

    /**
     * 抓取企业工商信息
     *
     * @param companyParam
     * @return
     * @throws Exception
     */
    default Company grabCompanyDetail(Company companyParam, boolean byRisk) throws Exception {
        if (companyParam == null || StringUtils.isBlank(companyParam.getCompId())) {
            return companyParam;
        }
        String companyId = companyParam.getCompId();
        String jsonData;
        JSONObject apiResult;
        String pageDoc = byRisk ? companyRiskDetailHtml(companyId) : companyDetailHtml(companyId);
        Preconditions.checkArgument(StringUtils.isNotBlank(pageDoc), "未获取到公司信息页面");
        Document document = Jsoup.parse(pageDoc);
        Element element = document.selectFirst("body > script");
        jsonData = element.html();
        String pageJsonBeginKey = "window.pageData =";
        String pageJsonEndKey = "window.isSpider";
        int pageJsonStartIndex = jsonData.indexOf(pageJsonBeginKey);
        int pageJsonEndIndex = jsonData.indexOf(pageJsonEndKey);
        if (pageJsonStartIndex > 0) {
            jsonData = jsonData.substring(pageJsonStartIndex + pageJsonBeginKey.length(), pageJsonEndIndex).trim();
            if (jsonData.endsWith(";")) {
                jsonData = jsonData.substring(0, jsonData.length() - 1).trim();
            }
        } else {
            jsonData = null;
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonData),
                "未能从公司信息页面获得window.pageData");
        JSONObject data = JacksonUtil.JSON_MAPPER.readValue(jsonData, JSONObject.class);
        Preconditions.checkArgument(data != null,
                "未能从公司信息页面获得window.pageData");
        Preconditions.checkArgument(data.getJSONObject("result") != null,
                "未能从公司信息页面获得window.pageData, data:%s", data);
        apiResult = byRisk ? data.getJSONObject("result").getJSONObject("baseInfo") : data.getJSONObject("result");
        applyCompanyJson(companyParam, apiResult);
        return companyParam;
    }

    /**
     * 抓取人员信息
     *
     * @param person
     * @return
     */
    default Person grabPersonSimpleInfo(Person person) throws Exception {
        if (person == null || StringUtils.isBlank(person.getPersonId())) {
            return person;
        }
        String personId = person.getPersonId();
        JSONObject apiResult = personSimpleInfo(personId);
        Preconditions.checkArgument(apiResult != null && apiResult.getJSONObject("data") != null,
                "接口返回空数据, apiResult=> %s", apiResult);
        JSONObject data = apiResult.getJSONObject("data");
        Person apiPerson = data.toJavaObject(Person.class);
        Preconditions.checkArgument(apiPerson != null && apiPerson.basicValid(),
                "接口返回空数据, apiResult=> %s", apiResult);
        person.setPersonName(apiPerson.getPersonName());
        return person;
    }

    /**
     * 抓取人员信息
     *
     * @param person
     * @return
     */
    default Person grabPersonDetail(Person person, boolean byRisk) throws Exception {
        if (person == null || StringUtils.isBlank(person.getPersonId())) {
            return person;
        }
        String personId = person.getPersonId();
        String pageDoc = byRisk ? personRiskDetailHtml(personId) : personDetailHtml(personId);
        Document document = Jsoup.parse(pageDoc);
        Element element = document.selectFirst("body > script");
        String jsonData = element.html();
        String pageJsonBeginKey = "window.pageData =";
        String pageJsonEndKey = "window.isSpider";
        int pageJsonStartIndex = jsonData.indexOf(pageJsonBeginKey);
        int pageJsonEndIndex = jsonData.indexOf(pageJsonEndKey);
        if (pageJsonStartIndex > 0) {
            jsonData = jsonData.substring(pageJsonStartIndex + pageJsonBeginKey.length(), pageJsonEndIndex).trim();
            if (jsonData.endsWith(";")) {
                jsonData = jsonData.substring(0, jsonData.length() - 1).trim();
            }
        } else {
            jsonData = null;
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonData),
                "未能从人员信息页面获得window.pageData");
        JSONObject data = JacksonUtil.JSON_MAPPER.readValue(jsonData, JSONObject.class);
        Preconditions.checkArgument(data != null,
                "未能从人员信息页面获得window.pageData");
        Preconditions.checkArgument(data.getJSONObject("result") != null,
                "未能从人员信息页面获得window.pageData, data:%s", data);
        JSONObject apiResult = data.getJSONObject("result");
        Person grabPerson = apiResult.toJavaObject(Person.class);
        person.mergeDetail(grabPerson);
        Preconditions.checkArgument(person.basicValid(),
                "未能从人员信息页面获得window.pageData, data:%s", data);
        if (person.hasPersonIntro()) {
            person.setJson_str(JSON.toJSONString(person));
        }
        return person;
    }
}
