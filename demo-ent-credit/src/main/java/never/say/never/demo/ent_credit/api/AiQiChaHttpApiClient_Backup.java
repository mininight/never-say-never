/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import never.say.never.demo.ent_credit.configure.EntCreditBeanUnit;
import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.service.EntCreditReadService;
import never.say.never.demo.ent_credit.util.StringKV;

import java.util.List;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-09-18
 */
public interface AiQiChaHttpApiClient_Backup extends HttpEntCreditApi {

    @Override
    default Company grabEntCreditBasic(Company compParam) throws Throwable {
        EntCreditReadService readService = EntCreditBeanUnit.AQC_BACKUP_0805.getReadService();
        return readService.getCompany(compParam);
    }

    @Override
    default StringKV grabEntIdByName(String entName) throws Throwable {
        Company company = getEntCreditBasic(Company.asParam().name(entName));
        StringKV keyVal = new StringKV();
        keyVal.setKey(entName);
        keyVal.setValue(company.getCompId());
        return keyVal;
    }

    @Override
    default List<CompanyStock> grabEntStock(Company compParam) throws Throwable {
        EntCreditReadService readService = EntCreditBeanUnit.AQC_BACKUP_0805.getReadService();
        return readService.getCompanyStock(compParam);
    }

    @Override
    default List<CompanyInvest> grabEntInvest(Company compParam) throws Throwable {
        EntCreditReadService readService = EntCreditBeanUnit.AQC_BACKUP_0805.getReadService();
        return readService.getCompanyInvest(compParam);
    }

    @Override
    default List<CompanyHolds> grabEntHolds(Company compParam) throws Throwable {
        EntCreditReadService readService = EntCreditBeanUnit.AQC_BACKUP_0805.getReadService();
        return readService.getCompanyHolds(compParam);
    }

    @Override
    default List<CompanyIndirectHolds> grabEntIndirectHolds(Company compParam) throws Throwable {
        EntCreditReadService readService = EntCreditBeanUnit.AQC_BACKUP_0805.getReadService();
        return readService.getCompanyIndirectHolds(compParam);
    }

    @Override
    default List<CompanyKeyPerson> grabEntKeyPerson(Company compParam) throws Throwable {
        EntCreditReadService readService = EntCreditBeanUnit.AQC_BACKUP_0805.getReadService();
        return readService.getCompanyKeyPerson(compParam);
    }

    @Override
    default List<CompanyChangeRecord> grabEntChangeRecord(Company compParam) throws Throwable {
        EntCreditReadService readService = EntCreditBeanUnit.AQC_BACKUP_0805.getReadService();
        return readService.getCompanyChangeRecords(compParam);
    }

    @Override
    default List<PersonCompany> grabPersonCompany(Person param) throws Throwable {
        EntCreditReadService readService = EntCreditBeanUnit.AQC_BACKUP_0805.getReadService();
        return readService.getPersonDetail(param.getPersonName(), param.getPersonId()).getCompanies();
    }
}
