/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Preconditions;
import never.say.never.demo.ent_credit.configure.EntCreditBeanUnit;
import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.service.EntCreditReadService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-12
 */
public class EntCreditReadServiceImpl implements EntCreditReadService {

    private final EntCreditBeanUnit beanUnit;

    public EntCreditReadServiceImpl(EntCreditBeanUnit beanUnit) {
        this.beanUnit = beanUnit;
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
        if (StringUtils.isNotBlank(company.getCompId())) {
            return beanUnit.getRepository().selectCompanyById(company.getCompId());
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(company.getEntName()), "参数无效");
        return beanUnit.getRepository().selectCompanyByName(company.getEntName());
    }

    @Override
    public Company getCompanyDetail(String compName, String compId) {
        Company company = getCompany(compId, compName);
        company.setStockList(getCompanyStock(company));
        company.setInvestList(getCompanyInvest(company));
        company.setKeyPersonList(getCompanyKeyPerson(company));
        company.setHoldsList(getCompanyHolds(company));
        company.setIndirectHoldsList(getCompanyIndirectHolds(company));
        company.setChangeRecordList(getCompanyChangeRecords(company));
        return company;
    }

    @Override
    public List<CompanyKeyPerson> getCompanyKeyPerson(Company company) {
        return beanUnit.getRepository().selectCompanyKeyPerson(company.getCompId());
    }

    @Override
    public List<CompanyStock> getCompanyStock(Company company) {
        return beanUnit.getRepository().selectCompanyStock(company.getCompId());
    }

    @Override
    public List<CompanyInvest> getCompanyInvest(Company company) {
        return beanUnit.getRepository().selectCompanyInvest(company.getCompId());
    }

    @Override
    public List<CompanyHolds> getCompanyHolds(Company company) {
        return beanUnit.getRepository().selectCompanyHolds(company.getCompId());
    }

    @Override
    public List<CompanyIndirectHolds> getCompanyIndirectHolds(Company company) {
        return beanUnit.getRepository().selectCompanyIndirectHolds(company.getCompId());
    }

    @Override
    public List<CompanyChangeRecord> getCompanyChangeRecords(Company company) {
        return beanUnit.getRepository().selectCompanyChangeRecord(company.getCompId());
    }

    @Override
    public Person getPerson(String id) {
        return beanUnit.getRepository().selectPerson(id);
    }

    @Override
    public Person getPerson(Person person) {
        return beanUnit.getRepository().selectPerson(person.getPersonId());
    }

    @Override
    public Person getPersonDetail(String personName, String personId) {
        Person person = getPerson(personId);
        if (StringUtils.isNotBlank(person.getJson_str())) {
            person = JSON.parseObject(person.getJson_str(), Person.class);
        }
        person.setCompanies(beanUnit.getRepository().selectPersonCompanyList(personId));
        return person;
    }
}
