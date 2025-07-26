/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.repository;

import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.enums.SourceType;
import never.say.never.demo.ent_credit.logger.ExecFailedLoggerRepository;

import java.util.List;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-04
 */
public interface EntCreditRepository extends ExecFailedLoggerRepository {

    SourceId lookupId(SourceId sourceId);

    List<Company> selectAllCompany();

    List<Person> selectAllPerson();

    void saveCompany(Company company);

    void saveCompanyStock(List<CompanyStock> companyStockList);

    void saveCompanyKeyPerson(List<CompanyKeyPerson> companyKeyPersonList);

    void saveCompanyChangeRecord(List<CompanyChangeRecord> companyChangeRecordList);

    void saveCompanyInvest(List<CompanyInvest> companyInvestList);

    void saveCompanyHolds(List<CompanyHolds> companyHoldsList);

    void saveCompanyIndirectHolds(List<CompanyIndirectHolds> companyIndirectHoldsList);

    void savePerson(Person person);

    void savePersonCompany(List<PersonCompany> personCompanyList);

    Company selectCompanyById(String compId);

    Company selectCompanyByName(String entName);

    List<CompanyStock> selectCompanyStock(String id);

    List<CompanyInvest> selectCompanyInvest(String id);

    List<CompanyKeyPerson> selectCompanyKeyPerson(String id);

    List<CompanyHolds> selectCompanyHolds(String id);

    List<CompanyIndirectHolds> selectCompanyIndirectHolds(String id);

    List<CompanyChangeRecord> selectCompanyChangeRecord(String id);

    Person selectPerson(String personId);

    List<PersonCompany> selectPersonCompanyList(String personId);

    List<PersonCompany> selectPersonCompanyListByLevel(int level);

    Integer selectMaxLevel();

    CompanyMiss selectCompanyMissByName(String companyName);

    void logCompanyMiss(String companyName);

    void delPersonExecFailed(String id);

    void delCompanyExecFailed(String id);

    void savePullLog(PullLog pullLog);

    void setPullFinished(String id, SourceType sourceType);

    void savePullLogSub(PullLogSub pullLogSub);

    List<PullLog> selectAllUnFinished();

    List<PullLog> selectAllFinished();

    void cleanExecFailed();
}
