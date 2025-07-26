/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.service.impl;

import never.say.never.demo.ent_credit.configure.EntCreditBeanUnit;
import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.service.EntCreditWriteService;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import static never.say.never.demo.ent_credit.enums.SourceChannel.ID_DELIMITER;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-12
 */
public class EntCreditWriteServiceImpl implements EntCreditWriteService {

    private final EntCreditBeanUnit beanUnit;

    public EntCreditWriteServiceImpl(EntCreditBeanUnit beanUnit) {
        this.beanUnit = beanUnit;
    }

    @Override
    public void saveCompany(Company company) {
        beanUnit.getRepository().saveCompany(company);
    }

    @Override
    public void savePerson(Person person) {
        beanUnit.getRepository().savePerson(person);
    }

    @Override
    public void savePersonCompanies(List<PersonCompany> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        PersonCompany personCompany;
        for (PersonCompany value : list) {
            personCompany = value;
            beanUnit.sendCompanyToCollector(
                    personCompany.getPersonName() + ID_DELIMITER + personCompany.getPersonId(),
                    personCompany.getCompName(), personCompany.getCompId());
        }
        beanUnit.getRepository().savePersonCompany(list);
    }

    @Override
    public void saveCompanyStock(List<CompanyStock> companyStockList) {
        beanUnit.getRepository().saveCompanyStock(companyStockList);
    }

    @Override
    public void saveCompanyInvest(List<CompanyInvest> companyInvestList) {
        beanUnit.getRepository().saveCompanyInvest(companyInvestList);
    }

    @Override
    public void saveCompanyKeyPerson(List<CompanyKeyPerson> companyKeyPersonList) {
        beanUnit.getRepository().saveCompanyKeyPerson(companyKeyPersonList);
    }

    @Override
    public void saveCompanyHolds(List<CompanyHolds> companyHolds) {
        beanUnit.getRepository().saveCompanyHolds(companyHolds);
    }

    @Override
    public void saveCompanyIndirectHolds(List<CompanyIndirectHolds> companyIndirectHolds) {
        beanUnit.getRepository().saveCompanyIndirectHolds(companyIndirectHolds);
    }

    @Override
    public void saveCompanyChangeRecord(List<CompanyChangeRecord> companyChangeRecords) {
        beanUnit.getRepository().saveCompanyChangeRecord(companyChangeRecords);
    }

}
