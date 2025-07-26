/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.service;

import never.say.never.demo.ent_credit.entity.*;

import java.util.List;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-12
 */
public interface EntCreditWriteService {

    void saveCompany(Company company);

    void savePerson(Person person);

    void savePersonCompanies(List<PersonCompany> personCompanyList);

    void saveCompanyChangeRecord(List<CompanyChangeRecord> companyChangeRecords);

    void saveCompanyHolds(List<CompanyHolds> companyHolds);

    void saveCompanyIndirectHolds(List<CompanyIndirectHolds> companyIndirectHolds);

    void saveCompanyInvest(List<CompanyInvest> companyInvestList);

    void saveCompanyKeyPerson(List<CompanyKeyPerson> companyKeyPersonList);

    void saveCompanyStock(List<CompanyStock> companyStockList);

}
