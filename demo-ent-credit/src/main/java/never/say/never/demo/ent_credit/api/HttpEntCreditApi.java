/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Preconditions;
import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.enums.PersonRole;
import never.say.never.demo.ent_credit.enums.SourceChannel;
import never.say.never.demo.ent_credit.enums.SourceType;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.util.StringKV;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-09
 */
public interface HttpEntCreditApi extends HttpApiClient {

    default SourceId lookupId(Company ent) {
        SourceId sourceId = ent.getSourceId();
        if (sourceId == null) {
            sourceId = new SourceId();
            sourceId.setChannel(SourceChannel.of(this).name());
            sourceId.setType(SourceType.company.name());
            sourceId.setName(ent.getEntName());
            sourceId.setValue(ent.getCompId());
            sourceId = HttpApiRequestContext.getCurrent().getBeanUnit().getRepository().lookupId(sourceId);
            ent.setSourceId(sourceId);
            ent.setCompId(sourceId.getId());
        }
        return sourceId;
    }

    default SourceId lookupId(Person person) {
        SourceId sourceId = person.getSourceId();
        if (sourceId == null) {
            sourceId = new SourceId();
            sourceId.setChannel(SourceChannel.of(this).name());
            sourceId.setType(SourceType.person.name());
            sourceId.setName(person.getPersonName());
            sourceId.setValue(person.getPersonId());
            Map<String, Set<PersonRole>> personSign = person.getCompanies().stream()
                    .collect(Collectors.toMap(
                            PersonCompany::getCompId,
                            personCompany -> PersonRole.extract(personCompany.getRelationShip()),
                            (o1, o2) -> {
                                o1.addAll(o2);
                                return o1;
                            }
                    ));
            sourceId.setPersonSign(personSign);
            sourceId = HttpApiRequestContext.getCurrent().getBeanUnit().getRepository().lookupId(sourceId);
            person.setSourceId(sourceId);
            person.setPersonId(sourceId.getId());
        }
        return sourceId;
    }

    default Company getEntCredit(Company compParam) throws Throwable {
        Company ent = getEntCreditBasic(compParam);
        ent.setStockList(grabEntStock(ent));
        ent.setInvestList(grabEntInvest(ent));
        ent.setHoldsList(grabEntHolds(ent));
        ent.setIndirectHoldsList(grabEntIndirectHolds(ent));
        ent.setKeyPersonList(grabEntKeyPerson(ent));
        ent.setChangeRecordList(grabEntChangeRecord(ent));
        // 关联公司
        Company legalCompanyInfo = ent.getLegalCompanyInfo();
        if (legalCompanyInfo != null) {
            ent.getRelateCompanyMap().put(legalCompanyInfo.getCompId(), legalCompanyInfo);
        }
        ent.getStockList().stream()
                .filter(item -> StringUtils.isBlank(item.getPersonId()))
                .forEach(item -> {
                    Company company = Company.asParam().id(item.getStockId()).name(item.getStockName());
                    company = getEntCreditBasic(company);
                    ent.getRelateCompanyMap().put(company.getCompId(), company);
                });
        ent.getInvestList().forEach(item -> {
            Company company = Company.asParam().id(item.getTo_compId()).name(item.getTo_compName());
            company = getEntCreditBasic(company);
            ent.getRelateCompanyMap().put(company.getCompId(), company);
        });
        ent.getHoldsList().forEach(item -> {
            Company company = Company.asParam().id(item.getTo_compId()).name(item.getTo_compName());
            company = getEntCreditBasic(company);
            ent.getRelateCompanyMap().put(company.getCompId(), company);
        });
        ent.getIndirectHoldsList().forEach(item -> {
            Company company = Company.asParam().id(item.getTo_compId()).name(item.getTo_compName());
            company = getEntCreditBasic(company);
            ent.getRelateCompanyMap().put(company.getCompId(), company);
        });
        // 关联人员
        List<Person> relatePersonList = new ArrayList<>();
        relatePersonList.add(ent.getLegalPersonInfo());
        relatePersonList.addAll(ent.getKeyPersonList());
        ent.getStockList().stream().filter(companyStock -> StringUtils.isNotBlank(companyStock.getPersonId()))
                .forEach(relatePersonList::add);
        for (Person person : relatePersonList) {
            if (person == null) {
                continue;
            }
            if (person.getPersonId().contains(person.getPersonName())) {
                continue;
            }
            List<PersonCompany> personCompanies = grabPersonCompany(person);
            person.setCompanies(personCompanies);
            lookupId(person);
            ent.getRelatePersonMap().put(person.getPersonId(), person);
            person.getCompanies().forEach(personCompany -> {
                Company company = personCompany.getCompany();
                if (company == null) {
                    String compId = personCompany.getCompId();
                    String entName = personCompany.getCompName();
                    company = getEntCreditBasic(Company.asParam().id(compId).name(entName));
                    personCompany.setCompany(company);
                }
                ent.getRelateCompanyMap().put(company.getCompId(), company);
            });
        }
        return ent;
    }

    default Company getEntCreditBasic(Company compParam) {
        try {
            Company company = grabEntCreditBasic(compParam);
            lookupId(company);
            String legalPersonName = company.getLegalPerson();
            // 法人
            String legalPersonId = company.getPersonId();
            if (StringUtils.isBlank(legalPersonId)) {
                legalPersonId = getEntPersonIdByName(company, legalPersonName, PersonRole.legal_person);
            }
            if (StringUtils.isNotBlank(legalPersonId)) {
                Person legalPerson = Person.asParam().id(legalPersonId).name(legalPersonName);
                company.setLegalPersonInfo(legalPerson);
            } else {
                Company legalCompany = getEntCreditBasic(Company.asParam().name(legalPersonName));
                company.setLegalCompanyInfo(legalCompany);
            }
            return company;
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new RuntimeException(t.getMessage(), t);
            }
        }
    }

    default String getEntPersonIdByName(Company compParam, String personName, PersonRole... roles) throws Throwable {
        SourceId sourceId = compParam.getSourceId();
        String personId = grabEntPersonIdByName(sourceId.getValue(), personName, roles);
        if (StringUtils.isBlank(personId)) {
            List<CompanyKeyPerson> keyPersonList = grabEntKeyPerson(compParam);
            keyPersonList = keyPersonList == null ? new ArrayList<>() : keyPersonList;
            keyPersonList = keyPersonList.stream()
                    .filter(keyPerson -> Objects.equals(keyPerson.getPersonName(), personName))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(keyPersonList)) {
                personId = null;
            } else if (keyPersonList.size() == 1) {
                personId = keyPersonList.get(0).getPersonId();
            } else {
                CompanyKeyPerson companyKeyPerson = keyPersonList.stream().filter(keyPerson -> {
                    Set<PersonRole> roleSet = PersonRole.extract(keyPerson.getPositionTitle());
                    return CollectionUtils.containsAny(roleSet, roles);
                }).findFirst().orElse(null);
                personId = companyKeyPerson == null ? null : companyKeyPerson.getPersonId();
            }
        }
        Preconditions.checkArgument(StringUtils.isBlank(personId), "未能获得'%s'人员'%s'的源ID",
                sourceId.getName(), personName);
        return personId;
    }

    default StringKV grabEntIdByName(String entName) throws Throwable {
        return null;
    }

    default String grabEntPersonIdByName(String entId, String name, PersonRole... roles) throws Throwable {
        return null;
    }

    default Company grabEntCreditBasic(Company compParam) throws Throwable {
        return null;
    }

    default List<CompanyStock> grabEntStock(Company compParam) throws Throwable {
        return null;
    }

    default List<CompanyInvest> grabEntInvest(Company compParam) throws Throwable {
        return null;
    }

    default List<CompanyHolds> grabEntHolds(Company compParam) throws Throwable {
        return null;
    }

    default List<CompanyIndirectHolds> grabEntIndirectHolds(Company compParam) throws Throwable {
        return null;
    }

    default List<CompanyKeyPerson> grabEntKeyPerson(Company compParam) throws Throwable {
        return null;
    }

    default List<CompanyChangeRecord> grabEntChangeRecord(Company compParam) throws Throwable {
        return null;
    }

    default List<PersonCompany> grabPersonCompany(Person param) throws Throwable {
        return null;
    }

    default void applyCompanyJson(Company compParam, JSONObject companyJson) {
    }
}
