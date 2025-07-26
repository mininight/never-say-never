/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Preconditions;
import never.say.never.demo.ent_credit.configure.EntCreditBeanUnit;
import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.enums.AiQiChaCompanyRelationType;
import never.say.never.demo.ent_credit.enums.AiQiChaPersonCompanyRelationType;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.http.HttpApiSLB;
import never.say.never.demo.ent_credit.http.HttpApiSLBPoints;
import never.say.never.demo.ent_credit.service.EntCreditHttpReadService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static never.say.never.demo.ent_credit.enums.AiQiChaCompanyRelationType.*;
import static never.say.never.demo.ent_credit.enums.AiQiChaPersonCompanyRelationType.*;
import static never.say.never.demo.ent_credit.enums.SourceChannel.ID_DELIMITER;
import static never.say.never.demo.ent_credit.http.HttpApiClientPanel.AiQiCha;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-12
 */
public class EntCreditHttpReadServiceImpl implements EntCreditHttpReadService {

    private final EntCreditBeanUnit beanUnit;
    private final HttpApiSLBPoints httpApiSLBPoints;

    public EntCreditHttpReadServiceImpl(EntCreditBeanUnit beanUnit) {
        this.beanUnit = beanUnit;
        this.httpApiSLBPoints = this.beanUnit.getHttpApiSLBPoints();
    }

    @Override
    public Company getCompany(String compName, String compId) {
        Company company = new Company();
        company.setCompId(compId);
        company.setEntName(compName);
        return getCompany(company);
    }

    @Override
    public Company getCompany(Company company) {
        Preconditions.checkArgument(StringUtils.isNotBlank(company.getEntName())
                || StringUtils.isNotBlank(company.getCompId()), "参数异常");
        return httpApiSLBPoints.getCompanyInfo().apply(company.errId(), company);
    }

    /**
     * 拉取公司信息
     *
     * @param compId
     * @return
     */
    @Override
    public Company getCompanyDetail(String compName, String compId) throws Exception {
        if (StringUtils.isBlank(compId)) {
            return null;
        }
        Thread.sleep(1000); // TODO
        Integer level = HttpApiRequestContext.getCurrent().getLevel();
        Company companyObj = new Company();
        companyObj.setEntName(compName);
        companyObj.setCompId(compId);
        companyObj.setLevel(level);
        String logId = companyObj.errId();
        try {
            companyObj = getCompany(companyObj);
            if (companyObj == null || !companyObj.basicValid()) {
                beanUnit.getRecorder().logCompanyErr(logId, "getCompany", "工商信息未能获取到");
                return companyObj;
            }
        } catch (Exception e) {
            beanUnit.getRecorder().logCompanyErr(logId, "getCompany", "工商信息未能获取到", e);
            return companyObj;
        }
        //
        companyObj.setLevel(level);
        Company company = companyObj;
        AiQiCha.updateReferer("https://aiqicha.baidu.com/company_detail_" + compId);
        // 最新公示、公司工商登记的股东信息
        company.setStockList(getCompanyStock(company));
        // 主要人员
        company.setKeyPersonList(getCompanyKeyPerson(company));
        // 最终受益人 TODO
        // 对外投资
        company.setInvestList(getCompanyInvest(company));
        // 控股企业
        company.setHoldsList(getCompanyHolds(company));
        // 间接持股企业
        company.setIndirectHoldsList(getCompanyIndirectHolds(company));
        // 变更记录
        company.setChangeRecordList(getCompanyChangeRecords(company));
        //
        if (company.getLegalPerson() != null && (company.getLegalPerson().contains("公司") ||
                company.getLegalPerson().contains("企业") || company.getLegalPerson().contains("投资")
                || company.getLegalPerson().contains("基金"))) {
            beanUnit.sendCompanyToCollector(logId, company.getLegalPerson(), company.getPersonId());
        } else {
            Person legalPerson = new Person();
            legalPerson.setPersonId(company.getPersonId());
            legalPerson.setPersonName(company.getLegalPerson());
            beanUnit.sendPersonToCollector(logId, legalPerson);
        }
        return company;
    }

    @Override
    public List<CompanyKeyPerson> getCompanyKeyPerson(Company company) {
        List<CompanyKeyPerson> list = new ArrayList<>();
        pullCompanyRelationData(company, companyKeyPerson, 1, list);
        return list.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<CompanyStock> getCompanyStock(Company company) {
        List<CompanyStock> list = new ArrayList<>();
        pullCompanyRelationData(company, companyShares1, 1, list);
        pullCompanyRelationData(company, companyShares2, 1, list);
        return list.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<CompanyInvest> getCompanyInvest(Company company) {
        List<CompanyInvest> list = new ArrayList<>();
        pullCompanyRelationData(company, companyInvest, 1, list);
        return list.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<CompanyHolds> getCompanyHolds(Company company) {
        List<CompanyHolds> list = new ArrayList<>();
        pullCompanyRelationData(company, companyHolds, 1, list);
        return list.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<CompanyIndirectHolds> getCompanyIndirectHolds(Company company) {
        List<CompanyIndirectHolds> list = new ArrayList<>();
        pullCompanyRelationData(company, companyIndirectHolds, 1, list);
        return list.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<CompanyChangeRecord> getCompanyChangeRecords(Company company) {
        List<CompanyChangeRecord> list = new ArrayList<>();
        pullCompanyRelationData(company, companyChangeRecord, 1, list);
        return list.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public Person getPerson(String id) {
        Person person = new Person();
        person.setPersonId(id);
        return getPerson(person);
    }

    @Override
    public Person getPerson(Person person) {
        Preconditions.checkArgument(StringUtils.isNotBlank(person.getPersonId()), "人员ID缺失");
        return httpApiSLBPoints.getPersonInfo().apply(person.errId(), person);
    }

    /**
     * 拉取人员名下公司
     *
     * @param personId
     */
    @Override
    public Person getPersonDetail(String personName, String personId) throws Exception {
        if (StringUtils.isBlank(personId)) {
            return null;
        }
        Thread.sleep(1000); // TODO
        Person personObj = new Person();
        personObj.setPersonId(personId);
        personObj.setPersonName(personName);
        String logId = personObj.errId();
        try {
            personObj = getPerson(personObj);
            if (personObj == null || !personObj.basicValid()) {
                beanUnit.getRecorder().logPersonErr(logId, "getPerson", "人员信息未能获取到");
                return personObj;
            }
        } catch (Exception e) {
            beanUnit.getRecorder().logPersonErr(logId, "getPerson", "人员信息未能获取到", e);
            return personObj;
        }
        Person person = personObj;
        AiQiCha.updateReferer("https://aiqicha.baidu.com/person?personId=" + personId + "&subtab=personal-allenterprises");
        try {
            List<PersonCompany> list = personCompanyListFromPerson(person);
            //
            AiQiChaPersonCompanyRelationType[] relationTypes = AiQiChaPersonCompanyRelationType.values();
            for (AiQiChaPersonCompanyRelationType relationType : relationTypes) {
                pullPersonRelationCompanies(person, relationType, 1, list);
            }
            person.setCompanies(list);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return person;
    }

    /**
     * 拉取公司关联数据
     *
     * @param company
     * @param type
     */
    private <T> List<T> pullCompanyRelationData(Company company, AiQiChaCompanyRelationType type, int page,
                                                List<T> entityList) {
        Preconditions.checkArgument(StringUtils.isNotBlank(company.getCompId()), "公司ID缺失");
        String logId = company.errId();
        Method method = type.getApiMethod();
        JSONObject apiResult = type.touchApi(page, 100, company.getCompId());
        JSONObject data = apiResult.getJSONObject("data");
        if (data == null) {
            beanUnit.getRecorder().logCompanyErr(logId, "pullCompanyRelationData#" + method.getName(),
                    "远程接口报错, apiResult=> %s", apiResult);
            return entityList;
        }
        JSONArray jsonArray = data.getJSONArray("list");
        if (jsonArray == null) {
            return entityList;
        }
        int num = jsonArray.size();
        JSONObject item;
        T entity;
        for (int i = 0; i < num; i++) {
            item = jsonArray.getJSONObject(i);
            entity = item.toJavaObject(type.getEntityClass());
            // 
            if (entity instanceof CompanyInvest || entity instanceof CompanyHolds) {
                String compId = item.getString("pid");
                String compName = item.getString("entName");
                if (StringUtils.isBlank(compId)) {
                    continue;
                }
                if (entity instanceof CompanyInvest companyInvest) {
                    companyInvest.setCompId(company.getCompId());
                    companyInvest.setCompName(company.getEntName());
                    companyInvest.setTo_compId(compId);
                    companyInvest.setTo_compName(compName);
                }
                if (entity instanceof CompanyHolds companyHolds) {
                    companyHolds.setCompId(company.getCompId());
                    companyHolds.setCompName(company.getEntName());
                    companyHolds.setTo_compId(compId);
                    companyHolds.setTo_compName(compName);
                }
            }
            //
            if (entity instanceof HttpApiSLB.Res && !((HttpApiSLB.Res) entity).O_K()) {
                continue;
            }
            //
            switch (type) {
                case companyShares1:
                case companyShares2:
                    CompanyStock companyStock = (CompanyStock) entity;
                    if (StringUtils.isBlank(companyStock.getStockName())) {
                        beanUnit.getRecorder().logCompanyErr(company.errId(), type.name(),
                                "公司第 %s 个股东名称丢失", i + 1);
                        continue;
                    }
                    companyStock.setCompId(company.getCompId());
                    beanUnit.sendCompanyStockToCollector(company, companyStock);
                    break;
                case companyHolds:
                case companyIndirectHolds:
                    CompanyHolds companyHolds = (CompanyHolds) entity;
                    // 被直接控股的公司
                    beanUnit.sendCompanyToCollector(logId, companyHolds.getTo_compName(), companyHolds.getTo_compId());
                    // 投资路径
                    JSONArray pathData = item.getJSONArray("pathData");
                    if (CollectionUtils.isEmpty(pathData)) {
                        continue;
                    }
                    for (int j = 0; j < pathData.size(); j++) {
                        JSONObject pathDataItem = pathData.getJSONObject(j);
                        String compName = pathDataItem.getString("investComp");
                        String compId = pathDataItem.getString("pid");
                        if (StringUtils.isBlank(compId)) {
                            continue;
                        }
                        CompanyHolds holderInPath = new CompanyHolds();
                        companyHolds.setCompId(company.getCompId());
                        companyHolds.setCompName(company.getEntName());
                        companyHolds.setTo_compId(compId);
                        companyHolds.setTo_compName(compName);
                        entityList.add((T) holderInPath);
                        // 被直接控股的公司
                        beanUnit.sendCompanyToCollector(logId, compName, compId);
                    }
                    break;
                case companyKeyPerson:
                    CompanyKeyPerson companyKeyPerson = (CompanyKeyPerson) entity;
                    companyKeyPerson.setCompId(company.getCompId());
                    beanUnit.sendPersonToCollector(logId, companyKeyPerson);
                    break;
                case companyInvest:
                    CompanyInvest companyInvest = (CompanyInvest) entity;
                    // 被投资的公司
                    beanUnit.sendCompanyToCollector(logId, companyInvest.getTo_compName(), companyInvest.getTo_compId());
                    break;
                case companyChangeRecord:
                    CompanyChangeRecord companyChangeRecord = (CompanyChangeRecord) entity;
                    companyChangeRecord.setCompId(company.getCompId());
                    break;
                default:
                    break;
            }
            entityList.add(entity);
        }
        if (CollectionUtils.isEmpty(entityList)) {
            return entityList;
        }
        int totalNum = data.getIntValue("total");
        int totalPage = totalNum / 100;
        if (page < totalPage) {
            pullCompanyRelationData(company, type, page + 1, entityList);
        }
        return entityList;
    }


    /**
     * 拉取人-公司数据
     *
     * @param person
     */
    private List<PersonCompany> pullPersonRelationCompanies(Person person, AiQiChaPersonCompanyRelationType relationType,
                                                            int page, List<PersonCompany> personCompanyList) throws Exception{
        Method method = relationType.getApiMethod();
        JSONObject apiResult = relationType.touchApi(AiQiCha, page, 100, person.getPersonId());
        JSONObject data = apiResult.getJSONObject("data");
        if (data == null) {
            beanUnit.getRecorder().logPersonErr(person.errId(), "getPersonCompanies#" + method.getName(),
                    "远程接口报错, apiResult=> %s", apiResult);
            return personCompanyList;
        }
        JSONArray jsonArray = data.getJSONArray("list");
        if (jsonArray == null) {
            return personCompanyList;
        }
        int num = jsonArray.size();
        JSONObject rowData;
        String compId;
        String compName;
        Company company;
        PersonCompany personCompany;
        for (int i = 0; i < num; i++) {
            rowData = jsonArray.getJSONObject(i);
            compId = rowData.getString("pid");
            compName = rowData.getString("entName");
            // 拉取公司信息
            company = getCompanyDetail(compName, compId);
            if (company == null) {
                continue;
            }
            personCompany = buildPersonCompanyEntity(person, company, relationType);
            personCompanyList.add(personCompany);
            if (relationType == personHoldsEnterprises || relationType == personIndirectHoldsEnterprises) {
                JSONArray pathData = rowData.getJSONArray("pathData");
                for (int j = 0; j < pathData.size(); j++) {
                    try {
                        // 投资路径
                        JSONObject pathDetail = pathData.getJSONObject(j);
                        if (pathDetail == null || pathDetail.isEmpty()) {
                            continue;
                        }
                        JSONArray pathList = pathDetail.getJSONArray("pathList");
                        if (CollectionUtils.isEmpty(pathList)) {
                            continue;
                        }
                        for (int k = 0; k < pathList.size(); k++) {
                            compId = pathList.getJSONObject(k).getString("pid");
                            compName = pathList.getJSONObject(k).getString("investComp");
                            // 拉取公司信息
                            company = getCompanyDetail(compName, compId);
                            if (company == null) {
                                continue;
                            }
                            personCompany = buildPersonCompanyEntity(person, company, relationType);
                            if (personCompany == null) {
                                continue;
                            }
                            personCompanyList.add(personCompany);
                        }
                    } catch (Exception e) {
                        beanUnit.getRecorder().logPersonErr(person.errId() + ":" + compName + ID_DELIMITER + compId,
                                "getPersonCompanies#" + relationType + "#pathData", e.getMessage());
                    }
                }
            }
        }
        //
        int totalNum = data.getIntValue("total");
        int totalPage = totalNum / 100;
        if (page < totalPage) {
            pullPersonRelationCompanies(person, relationType, page + 1, personCompanyList);
        }
        return personCompanyList;
    }


    private List<PersonCompany> personCompanyListFromPerson(Person person) throws Exception{
        List<PersonCompany> list = new ArrayList<>();
        if (!person.hasPersonIntro()) {
            return list;
        }
        if (person.getPersonhead() == null || person.getPersonhead().getIntroduction() == null) {
            return list;
        }
        List<Person.CompItem> director = person.getPersonhead().getIntroduction().getDirector();
        List<Person.CompItem> isstockholde = person.getPersonhead().getIntroduction().getIsstockholde();
        List<Person.CompItem> legalPerson = person.getPersonhead().getIntroduction().getLegalPerson();
        addPersonCompItemsToPersonCompanyList(person, director, personIsDirectorsEnterprises, list);
        addPersonCompItemsToPersonCompanyList(person, isstockholde, personIsStockholderEnterprises, list);
        addPersonCompItemsToPersonCompanyList(person, legalPerson, personAsLegalEnterprises, list);
        return list;
    }

    private void addPersonCompItemsToPersonCompanyList(Person person, List<Person.CompItem> compItems,
                                                       AiQiChaPersonCompanyRelationType relationType,
                                                       List<PersonCompany> list) throws Exception{
        if (CollectionUtils.isEmpty(compItems)) {
            return;
        }
        for (Person.CompItem compItem : compItems) {
            Company company = getCompanyDetail(compItem.getName(), compItem.getPid());
            PersonCompany personCompany = buildPersonCompanyEntity(person, company, relationType);
            list.add(personCompany);
        }
    }
}
