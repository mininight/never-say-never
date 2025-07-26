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
import com.google.common.collect.ImmutableMap;
import feign.Body;
import feign.Param;
import feign.RequestLine;
import never.say.never.demo.ent_credit.api.annotation.DWH_DB;
import never.say.never.demo.ent_credit.api.dto.YiQiChaCompany;
import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.enums.PersonRole;
import never.say.never.demo.ent_credit.enums.SourceChannel;
import never.say.never.demo.ent_credit.exceptions.HttpApiFailedException;
import never.say.never.demo.ent_credit.http.HeaderTemplate;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.http.RefreshingCookie;
import never.say.never.demo.ent_credit.util.HttpApiPageDataGripper;
import never.say.never.demo.ent_credit.util.StringKV;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-10
 */
@DWH_DB(successEL = "${result.get('success')==true}")
public interface YiQiChaHttpApiClient extends HttpEntCreditApi {
    String BASE_HOST = "api.yiqicha.com";
    String BASE_URL = "https://" + BASE_HOST;

    HttpApiPageDataGripper PAGE_DATA_GRIPPER = HttpApiPageDataGripper.newInstance()
            .pageNoStart(1)
            .pageSize(50)
            .pageNoArgName("pageCurrent")
            .pageSizeArgName("pageSize")
            .totalPageFetcher(apiResult -> apiResult.getJSONObject("data").getIntValue("totalPage"))
            .pageDataFetcher(apiResult -> apiResult.getJSONObject("data").getJSONArray("list"));

    /**
     * 模糊匹配企业
     *
     * @return
     */
    @RequestLine("POST /search/api/v1/index/dropDownSearch")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject dropDownSearch(@Param("body") String body);

    /**
     * 模糊匹配企业
     *
     * @return
     */
    @RequestLine("POST /search/api/v1/middle/search")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject search(@Param("body") String body);

    /**
     * 企业信息
     *
     * @return
     */
    @RequestLine("GET /yqc/api/v1/business/comInfo?pid={pid}")
    @feign.Headers("Content-Type: application/json")
    JSONObject comInfo(@Param("pid") String pid);

    /**
     * 企业邮箱信息
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/business/getEmailInfo")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject comEmailInfo(@Param("body") String body);

    /**
     * 企业电话信息
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/business/getPhoneInfo")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject comPhoneInfo(@Param("body") String body);


    /**
     * 股东信息
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/business/shareholderList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject shareholderList(@Param("body") String body);

    /**
     * 历史股东信息
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/business/shareholderHistoryList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject shareholderHistoryList(@Param("body") String body);

    /**
     * 协同股东
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/business/associateShareholderList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject associateShareholderList(@Param("body") String body);

    /**
     * 主要人员
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/business/personList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject personList(@Param("body") String body);

    /**
     * 历史主要人员
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/business/historyPersonList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject historyPersonList(@Param("body") String body);

    /**
     * 对外投资
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/business/investList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject investList(@Param("body") String body);

    /**
     * 变更信息
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/business/alterList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject alterList(@Param("body") String body);


    /**
     * 控股企业
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/business/controlEpList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject controlEpList(@Param("body") String body);

    /**
     * 间接控股企业
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/business/indirectEpList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject indirectEpList(@Param("body") String body);

    /**
     * 疑似企业
     *
     * @return
     */
    @Deprecated
    @RequestLine("POST /yqc/api/v1/business/suspectRelList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject suspectRelList(@Param("body") String body);


    /**
     * 人员关联企业
     *
     * @return
     */
    @RequestLine("POST /yqc/person/baseInfo/selectPersonRelateEntList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject selectPersonRelateEntList(@Param("body") String body);

    /**
     * 人员任职、投资企业 => queryType：1(法人)，2（投资），9（任职）
     *
     * @return
     */
    @RequestLine("POST /yqc/person/baseInfo/selectPersonInvestInfoList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject selectPersonInvestInfoList(@Param("body") String body);


    /**
     * 人员控股企业
     *
     * @return
     */
    @RequestLine("POST /yqc/person/baseInfo/selectPersonControllerEntList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject selectPersonControllerEntList(@Param("body") String body);

    /**
     * 人员最终受益企业 => type:1（所有人）,2（自然人）
     *
     * @return
     */
    @RequestLine("POST /yqc/person/baseInfo/selectFinalBenefitList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject selectPersonFinalBenefitList(@Param("body") String body);

    /**
     * 人员间接持股企业
     *
     * @return
     */
    @RequestLine("POST /yqc/person/baseInfo/selectIndirectHolderEntList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject selectPersonIndirectHolderEntList(@Param("body") String body);

    /**
     * 公司人员ID
     *
     * @return
     */
    @RequestLine("POST /yqc/api/v1/person/getPersonId")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject getPersonId(@Param("body") String body);

    /**
     * 人员合作伙伴
     *
     * @return
     */
    @RequestLine("POST /yqc/person/baseInfo/selectPartnerList")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject selectPersonPartnerList(@Param("body") String body);

    @Override
    default void prepareHeaders(HttpApiRequestContext context) {
        context.getCustomHeaders().putAll(HeaderTemplate.YiQiCha_header.getKvMap());
        context.updateHeader("Host", BASE_HOST);
        context.updateHeader("Origin", BASE_URL);
        context.updateHeader("token", RefreshingCookie.YiQiCha_cookie.val());
    }

    @Override
    default StringKV grabEntIdByName(String entName) throws Throwable {
        Preconditions.checkArgument(StringUtils.isNotBlank(entName), "公司名称缺失");
        StringKV result = new StringKV();
        result.setKey(entName);
        String compId;
        JSONObject query = new JSONObject();
        query.put("pageCurrent", 1);
        query.put("pageSize", 10);
        query.put("keyword", entName);
        query.put("searchState", 1);
        JSONObject apiResult = checkJsonResult(() -> dropDownSearch(query.toJSONString()));
        compId = apiResult.getJSONObject("data").getJSONArray("leftList").toJavaList(JSONObject.class).stream()
                .filter(row -> entName.equals(row.getString("entNameNormal")))
                .map(row -> row.getString("pid"))
                .findFirst().orElse(null);
        if (StringUtils.isBlank(compId)) {
            apiResult = checkJsonResult(() -> search(query.toJSONString()));
            compId = apiResult.getJSONObject("data").getJSONArray("list").toJavaList(JSONObject.class).stream()
                    .filter(row -> {
                        String entNameNormal = row.getString("entNameNormal");
                        if (entName.equals(entNameNormal)) {
                            return true;
                        }
                        List<JSONObject> tags = row.getJSONArray("tags").toJavaList(JSONObject.class);
                        if (CollectionUtils.isEmpty(tags)) {
                            return false;
                        }
                        for (JSONObject tag : tags) {
                            List<JSONObject> historyTagEntityList = tag.getJSONArray("historyTagEntityList")
                                    .toJavaList(JSONObject.class);
                            if (CollectionUtils.isEmpty(historyTagEntityList)) {
                                continue;
                            }
                            for (JSONObject historyTag : historyTagEntityList) {
                                if (entName.equals(historyTag.getString("historyName"))) {
                                    result.setKey(entNameNormal);
                                    return true;
                                }
                            }
                        }
                        return false;
                    })
                    .map(row -> row.getString("pid"))
                    .findFirst().orElse(null);
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(compId), "无法获得%s渠道 '%s' 的ID",
                SourceChannel.of(this).name(), entName);
        result.setValue(compId);
        return result;
    }

    @Override
    default String grabEntPersonIdByName(String entId, String name, PersonRole... roles) {
        return getPersonId(JSON.toJSONString(ImmutableMap.of(
                "personName", name,
                "pid", entId
        ))).getString("data");
    }

    @Override
    default Company grabEntCreditBasic(Company compParam) throws Throwable {
        Preconditions.checkArgument(compParam != null, "无效的参数");
        String compId = compParam.getCompId();
        if (StringUtils.isBlank(compId)) {
            compId = grabEntIdByName(compParam.getEntName()).getValue();
        }
        if (StringUtils.isBlank(compId)) {
            return compParam;
        }
        String pid = compId;
        JSONObject apiResult = checkJsonResult(() -> comInfo(pid));
        applyCompanyJson(compParam, apiResult.getJSONObject("data"));
        compParam.setCompId(pid);
        // 邮箱
        JSONObject query = new JSONObject();
        query.put("pid", pid);
        query.put("pageCurrent", 1);
        query.put("pageSize", 10);
        apiResult = checkJsonResult(() -> comEmailInfo(query.toJSONString()));
        JSONArray list = apiResult.getJSONObject("data").getJSONArray("list");
        if (CollectionUtils.isNotEmpty(list)) {
            compParam.setEmail(list.toJavaList(JSONObject.class).stream()
                    .map(em -> em.getString("email")).collect(Collectors.joining("; ")));
        }
        // 电话
        apiResult = checkJsonResult(() -> comPhoneInfo(query.toJSONString()));
        list = apiResult.getJSONObject("data").getJSONArray("list");
        if (CollectionUtils.isNotEmpty(list)) {
            compParam.setTelephone(list.toJavaList(JSONObject.class).stream()
                    .map(em -> em.getString("phone")).collect(Collectors.joining("; ")));
        }
        //
        return compParam;
    }

    @Override
    default List<CompanyStock> grabEntStock(Company compParam) throws Throwable {
        SourceId sourceId = compParam.getSourceId();
        String pid = sourceId.getValue();
        JSONObject body = new JSONObject();
        body.put("pid", pid);
        body.put("isSecondLevel", false);
        body.put("startRow", 0);
        List<CompanyStock> result = pullPagedEntityItems(body, item -> jsonToCompanyStock(item, sourceId),
                () -> shareholderList(body.toJSONString()));
        List<CompanyStock> list1 = pullPagedEntityItems(body, item -> jsonToCompanyStock(item, sourceId),
                () -> shareholderHistoryList(body.toJSONString()));
        List<CompanyStock> list2 = pullPagedEntityItems(body, item -> jsonToCompanyStock(item, sourceId),
                () -> associateShareholderList(body.toJSONString()));
        result.addAll(list1);
        result.addAll(list2);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(result.stream()
                .collect(Collectors.toMap(CompanyStock::getStockId, Function.identity(), (o1, o2) -> o1))
                .values()
        );
    }

    @Override
    default List<CompanyHolds> grabEntHolds(Company compParam) throws Throwable {
        SourceId sourceId = compParam.getSourceId();
        String pid = sourceId.getValue();
        JSONObject body = new JSONObject();
        body.put("pid", pid);
        body.put("isSecondLevel", false);
        body.put("startRow", 0);
        List<List<CompanyHolds>> result = pullPagedEntityItems(body, item -> jsonToCompanyHolds(item, sourceId),
                () -> controlEpList(body.toJSONString()));
        return new ArrayList<>(result.stream().flatMap(Collection::stream)
                .collect(Collectors.toMap(CompanyHolds::getTo_compName, Function.identity(), (o1, o2) -> o1))
                .values()
        );
    }

    @Override
    default List<CompanyIndirectHolds> grabEntIndirectHolds(Company compParam) throws Throwable {
        SourceId sourceId = compParam.getSourceId();
        String pid = sourceId.getValue();
        JSONObject body = new JSONObject();
        body.put("pid", pid);
        body.put("isSecondLevel", false);
        body.put("startRow", 0);
        List<CompanyIndirectHolds> result = pullPagedEntityItems(body, item -> jsonToCompanyIndirectHolds(item, sourceId),
                () -> indirectEpList(body.toJSONString()));
        return new ArrayList<>(result.stream()
                .collect(Collectors.toMap(CompanyIndirectHolds::getTo_compName, Function.identity(), (o1, o2) -> o1))
                .values()
        );
    }

    @Override
    default List<CompanyInvest> grabEntInvest(Company compParam) throws Throwable {
        SourceId sourceId = compParam.getSourceId();
        String pid = sourceId.getValue();
        JSONObject body = new JSONObject();
        body.put("pid", pid);
        body.put("isSecondLevel", false);
        body.put("startRow", 0);
        List<CompanyInvest> result = pullPagedEntityItems(body, item -> jsonToCompanyInvest(item, sourceId),
                () -> investList(body.toJSONString()));
        return new ArrayList<>(result.stream()
                .collect(Collectors.toMap(CompanyInvest::getTo_compName, Function.identity(), (o1, o2) -> o1))
                .values()
        );
    }

    @Override
    default List<CompanyKeyPerson> grabEntKeyPerson(Company compParam) throws Throwable {
        SourceId sourceId = compParam.getSourceId();
        String pid = sourceId.getValue();
        JSONObject body = new JSONObject();
        body.put("pid", pid);
        body.put("isSecondLevel", false);
        body.put("startRow", 0);
        List<CompanyKeyPerson> result = pullPagedEntityItems(body, item -> jsonToCompanyKeyPerson(item, sourceId),
                () -> personList(body.toJSONString()));
        List<CompanyKeyPerson> history = pullPagedEntityItems(body, item -> jsonToCompanyKeyPerson(item, sourceId),
                () -> historyPersonList(body.toJSONString()));
        result.addAll(history);
        return new ArrayList<>(result.stream()
                .collect(Collectors.toMap(CompanyKeyPerson::getPersonId, Function.identity(), (o1, o2) -> o1))
                .values()
        );
    }

    @Override
    default List<CompanyChangeRecord> grabEntChangeRecord(Company compParam) throws Throwable {
        SourceId sourceId = compParam.getSourceId();
        String pid = sourceId.getValue();
        JSONObject body = new JSONObject();
        body.put("pid", pid);
        body.put("isSecondLevel", false);
        body.put("startRow", 0);
        return pullPagedEntityItems(body,
                item -> jsonToCompanyChangeRecord(item, sourceId),
                () -> alterList(body.toJSONString())
        );
    }

    @Override
    default List<PersonCompany> grabPersonCompany(Person personParam) throws Throwable {
        JSONObject body = new JSONObject();
        body.put("personId", personParam.getPersonId());
        body.put("startRow", 0);
        // 控股
        List<PersonCompany> companies = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item, PersonRole.invest_control),
                () -> selectPersonControllerEntList(body.toJSONString())
        );
        // 间接持股
        List<PersonCompany> companies0 = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item, PersonRole.indirect_holder),
                () -> selectPersonIndirectHolderEntList(body.toJSONString())
        );
        // 关联企业
        body.put("isQueryHistory", 1);
        List<PersonCompany> companies1 = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item),
                () -> selectPersonRelateEntList(body.toJSONString())
        );
        body.put("isQueryHistory", 2);
        List<PersonCompany> companies2 = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item),
                () -> selectPersonRelateEntList(body.toJSONString())
        );

        // 法人
        body.put("queryType", 1);
        body.put("isQueryHistory", 1);
        List<PersonCompany> companies3 = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item),
                () -> selectPersonInvestInfoList(body.toJSONString())
        );
        body.put("isQueryHistory", 2);
        List<PersonCompany> companies4 = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item),
                () -> selectPersonInvestInfoList(body.toJSONString())
        );

        // 投资
        body.put("queryType", 2);
        body.put("isQueryHistory", 1);
        List<PersonCompany> companies5 = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item),
                () -> selectPersonInvestInfoList(body.toJSONString())
        );
        body.put("isQueryHistory", 2);
        List<PersonCompany> companies6 = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item),
                () -> selectPersonInvestInfoList(body.toJSONString())
        );
        // 任职
        body.put("queryType", 9);
        body.put("isQueryHistory", 1);
        List<PersonCompany> companies7 = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item),
                () -> selectPersonInvestInfoList(body.toJSONString())
        );
        body.put("isQueryHistory", 2);
        List<PersonCompany> companies8 = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item),
                () -> selectPersonInvestInfoList(body.toJSONString())
        );
        // 受益人
        body.remove("queryType");
        body.remove("isQueryHistory");
        body.put("type", 1);
        body.put("sort", "desc");
        List<PersonCompany> companies9 = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item),
                () -> selectPersonFinalBenefitList(body.toJSONString())
        );
        body.put("type", 2);
        List<PersonCompany> companies10 = pullPagedEntityItems(body,
                item -> jsonToPersonCompany(personParam, item),
                () -> selectPersonFinalBenefitList(body.toJSONString())
        );
        companies.addAll(companies0);
        companies.addAll(companies1);
        companies.addAll(companies2);
        companies.addAll(companies3);
        companies.addAll(companies4);
        companies.addAll(companies5);
        companies.addAll(companies6);
        companies.addAll(companies7);
        companies.addAll(companies8);
        companies.addAll(companies9);
        companies.addAll(companies10);
        // 合作伙伴  TODO
        return new ArrayList<>(companies.stream().collect(Collectors.toMap(
                PersonCompany::getCompName,
                Function.identity(),
                (o1, o2) -> {
                    Set<String> s1 = new HashSet<>();
                    Collections.addAll(s1, o1.getRelationShip().split(","));
                    Collections.addAll(s1, o2.getRelationShip().split(","));
                    o1.setRelationShip(String.join(",", s1));
                    return o1;
                }
        )).values());
    }

    default CompanyStock jsonToCompanyStock(JSONObject item, SourceId sourceId) throws Throwable {
        CompanyStock stock = new CompanyStock();
        stock.setCompId(sourceId.getId());
        String stockId = item.getString("invPid");
        String stockName = item.getString("invName");
        if (item.getIntValue("isCompany") == 2) {
            stockId = item.getJSONObject("relateEntStatInfo").getString("personId");
            stock.setPersonId(stockId);
            stock.setPersonName(stockName);
        } else {
            if (StringUtils.isBlank(stock.getStockId())) {
                StringKV kv = grabEntIdByName(stockName);
                stockId = kv.getValue();
                stockName = kv.getKey();
            }
        }
        stock.setStockId(stockId);
        stock.setStockName(stockName);
        stock.setSubMoney(item.getString("invSubConAm"));
        stock.setSubRate(item.getString("benefitShare"));
        return stock;
    }

    default List<CompanyHolds> jsonToCompanyHolds(JSONObject item, SourceId sourceId) {
        List<CompanyHolds> result = new ArrayList<>();
        CompanyHolds holds = new CompanyHolds();
        holds.setCompId(sourceId.getId());
        holds.setCompName(sourceId.getName());
        String childEntPid = item.getString("childEntPid");
        String childEntName = item.getString("childEntName");
        holds.setTo_compId(childEntPid);
        holds.setTo_compName(childEntName);
        result.add(holds);
        item.getJSONArray("linkInfo").toJavaList(JSONObject.class).stream()
                .flatMap(jsonObject -> jsonObject.getJSONArray("linkList").toJavaList(JSONObject.class).stream())
                .map(jsonObject -> {
                    CompanyHolds subHolds = new CompanyHolds();
                    subHolds.setCompId(holds.getCompId());
                    subHolds.setCompName(holds.getCompName());
                    String pid = jsonObject.getString("pid");
                    String invName = jsonObject.getString("invName");
                    subHolds.setTo_compId(pid);
                    subHolds.setTo_compName(invName);
                    return subHolds;
                }).forEach(result::add);
        return result;
    }

    default CompanyIndirectHolds jsonToCompanyIndirectHolds(JSONObject item, SourceId sourceId) {
        CompanyIndirectHolds holds = new CompanyIndirectHolds();
        holds.setCompId(sourceId.getId());
        holds.setCompName(sourceId.getName());
        String childEntPid = item.getString("childEntPid");
        String childEntName = item.getString("childEntName");
        holds.setTo_compId(childEntPid);
        holds.setTo_compName(childEntName);
        return holds;
    }

    default CompanyInvest jsonToCompanyInvest(JSONObject item, SourceId sourceId) {
        CompanyInvest holds = new CompanyInvest();
        holds.setCompId(sourceId.getId());
        holds.setCompName(sourceId.getName());
        String childEntPid = item.getString("childEntPid");
        String childEntName = item.getString("childEntName");
        holds.setTo_compId(childEntPid);
        holds.setTo_compName(childEntName);
        return holds;
    }

    default CompanyKeyPerson jsonToCompanyKeyPerson(JSONObject item, SourceId sourceId) {
        CompanyKeyPerson keyPerson = new CompanyKeyPerson();
        keyPerson.setCompId(sourceId.getId());
        String personName = item.getString("name");
        String personTitle = item.getString("positionCn");
        String personId;
        String haveCompNum = "";
        JSONObject relateEntStatInfo = item.getJSONObject("relateEntStatInfo");
        if (relateEntStatInfo != null && !relateEntStatInfo.isEmpty()) {
            personId = relateEntStatInfo.getString("personId");
            haveCompNum = relateEntStatInfo.getString("relateEntCount");
        } else {
            personId = keyPerson.getCompId() + personName;
        }
        keyPerson.setPersonName(personName);
        keyPerson.setPersonId(personId);
        keyPerson.setPositionTitle(personTitle);
        keyPerson.setHaveCompNum(haveCompNum);
        return keyPerson;
    }

    default CompanyChangeRecord jsonToCompanyChangeRecord(JSONObject item, SourceId sourceId) {
        CompanyChangeRecord record = new CompanyChangeRecord();
        record.setCompId(sourceId.getId());
        record.setDate(item.getString("altDateDesc"));
        record.setFieldName(item.getString("altTypeDesc"));
        record.setOldValue(item.getString("altBefore"));
        record.setNewValue(item.getString("altAfter"));
        return record;
    }

    default PersonCompany jsonToPersonCompany(Person person, JSONObject item) throws Throwable {
        return jsonToPersonCompany(person, item, null);
    }

    default PersonCompany jsonToPersonCompany(Person person, JSONObject item, PersonRole personRole) throws Throwable {
        String entName = item.getString("entName");
        String pid = item.getString("pid");
        String position;
        if (personRole != null) {
            position = personRole.getKeywords().get(0);
        } else {
            position = item.containsKey("position") ? item.getString("position") : "";
            if (StringUtils.isNotBlank(position)) {
                position = position.replaceAll("，", ",");
                position = position.replaceAll("/", ",").trim();
            }
            String benefitType = item.containsKey("benefitType") ? item.getString("benefitType") : "";
            if (StringUtils.isNotBlank(benefitType)) {
                benefitType = benefitType.replaceAll("，", ",");
                benefitType = benefitType.replaceAll("/", ",").trim();
            }
            String officeType = item.containsKey("officeType") ? item.getString("officeType") : "";
            if (StringUtils.isNotBlank(officeType)) {
                officeType = officeType.replaceAll("，", ",");
                officeType = officeType.replaceAll("/", ",").trim();
            }
            List<String> posTokenList = new ArrayList<>();
            Collections.addAll(posTokenList, position.split(","));
            Collections.addAll(posTokenList, benefitType.split(","));
            Collections.addAll(posTokenList, officeType.split(","));
            position = posTokenList.stream().filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(","));
        }
        Company company = getEntCreditBasic(Company.asParam().id(pid).name(entName));
        return PersonCompany.of(person, company, position);
    }

    @Override
    default JSONObject checkJsonResult(ExecSupplier<JSONObject> supplier) throws Throwable {
        JSONObject apiResult = supplier.get();
        apiResult = apiResult == null ? new JSONObject() : apiResult;
        if (!apiResult.getBoolean("success")) {
            throw new HttpApiFailedException(200, String.format("接口无法访问，apiResult:%s", apiResult.toJSONString()));
        }
        return apiResult;
    }

    @Override
    default HttpApiPageDataGripper getPageDataGripper() {
        return PAGE_DATA_GRIPPER;
    }

    @Override
    default void applyCompanyJson(Company compParam, JSONObject companyJson) {
        if (companyJson == null || companyJson.isEmpty()) {
            return;
        }
        YiQiChaCompany yiQiChaCompany = companyJson.toJavaObject(YiQiChaCompany.class);
        compParam.setOpenStatus(yiQiChaCompany.getEntStatusDesc());
        compParam.setPreEntName(yiQiChaCompany.historyName());
        compParam.setUnifiedCode(yiQiChaCompany.getUncid());
        compParam.setRegNo(yiQiChaCompany.getRegNo());
        compParam.setOrgNo(yiQiChaCompany.getOrgCode());
        compParam.setRegAddr(yiQiChaCompany.getAddress());
        compParam.setDistrict(compParam.getRegAddr());
        compParam.setIndustry(yiQiChaCompany.industry());
        compParam.setScope(yiQiChaCompany.getOpScope());
        compParam.setStartDate(yiQiChaCompany.getEsDateDesc());
        compParam.setOpenTime(yiQiChaCompany.getOpFromDesc() + " 至 " + yiQiChaCompany.getOpToDesc());
        compParam.setRegCapital(yiQiChaCompany.getRegCap() + yiQiChaCompany.getRegCapCur());
        compParam.setAuthority(yiQiChaCompany.getRegOrgDesc());
        compParam.setAnnualDate(yiQiChaCompany.getApprDateDesc());
        String legalPersonName = yiQiChaCompany.getLegalPerson();
        JSONObject relateEntStatInfo = yiQiChaCompany.getRelateEntStatInfo();
        if (relateEntStatInfo != null) {
            legalPersonName = relateEntStatInfo.getString("personName");
            String personId = relateEntStatInfo.getString("personId");
            Integer entCount = relateEntStatInfo.getInteger("relateEntCount");
            compParam.setLegalPerson(legalPersonName);
            compParam.setPersonId(personId);
            compParam.setLegalCompNum(entCount);
        }
        compParam.setLegalPerson(legalPersonName);
    }
}
