/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.service;

import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.enums.AiQiChaPersonCompanyRelationType;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-14
 */
public interface EntCreditHttpReadService {

    Company getCompany(String compName, String compId);

    Company getCompany(Company company);

    Company getCompanyDetail(String compName, String compId) throws Exception;

    List<CompanyKeyPerson> getCompanyKeyPerson(Company company);

    List<CompanyStock> getCompanyStock(Company company);

    List<CompanyInvest> getCompanyInvest(Company company);

    List<CompanyHolds> getCompanyHolds(Company company);

    List<CompanyIndirectHolds> getCompanyIndirectHolds(Company company);

    List<CompanyChangeRecord> getCompanyChangeRecords(Company company);

    Person getPerson(String id);

    Person getPerson(Person person);

    Person getPersonDetail(String personName, String personId) throws Exception;

    /**
     * @param person
     * @param company
     * @param relationType
     * @return
     */
    default PersonCompany buildPersonCompanyEntity(Person person, Company company,
                                                   AiQiChaPersonCompanyRelationType relationType) {
        if (person == null || company == null) {
            return null;
        }
        HttpApiRequestContext httpApiRequestContext = HttpApiRequestContext.getCurrent();
        Integer level = httpApiRequestContext == null ? company.getLevel() : httpApiRequestContext.getLevel();
        PersonCompany personCompany = new PersonCompany();
        personCompany.setPersonId(person.getPersonId());
        personCompany.setCompId(company.getCompId());
        personCompany.setPersonName(person.getPersonName());
        personCompany.setRelationShip(relationType.getDescribe() + ",");
        personCompany.setCompName(company.getEntName());
        personCompany.setPreCompName(company.getPreEntName());
        personCompany.setUnifiedCode(company.getUnifiedCode());
        if (StringUtils.isNotBlank(company.getRegCode())) {
            personCompany.setRegNo(company.getRegCode());
        } else {
            personCompany.setRegNo(company.getRegNo());
        }
        personCompany.setLogoWord(company.getEntLogoWord());
        personCompany.setRegion(company.getDistrict());
        personCompany.setRegAddr(company.getRegAddr());
        String startDate = company.getStartDate();
        if (StringUtils.isBlank(startDate) || startDate.length() < 8) {
            startDate = null;
        }
        personCompany.setStartDate(startDate);
        personCompany.setRegCap(company.getRegCapital());
        personCompany.setOpenStatus(company.getOpenStatus());
        personCompany.setTelephone(company.getTelephone());
        personCompany.setMailbox(company.getEmail());
        personCompany.setBankInfo(company.getBankInfo());
        personCompany.setLevel(level);
        return personCompany;
    }
}
