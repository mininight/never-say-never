/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.manger;

import never.say.never.demo.ent_credit.configure.EntCreditBeanUnit;
import never.say.never.demo.ent_credit.entity.Company;
import never.say.never.demo.ent_credit.entity.CompanyMiss;
import never.say.never.demo.ent_credit.entity.Person;
import never.say.never.demo.ent_credit.entity.PullLog;
import never.say.never.demo.ent_credit.enums.SourceType;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import java.time.LocalDateTime;
import java.util.*;

import static never.say.never.demo.ent_credit.configure.EntCreditBeanUnit.*;
import static never.say.never.demo.ent_credit.enums.SourceChannel.ID_DELIMITER;
import static never.say.never.demo.ent_credit.enums.SourceType.company;
import static never.say.never.demo.ent_credit.enums.SourceType.person;
import static never.say.never.demo.ent_credit.http.HttpApiClientPanel.AiQiCha;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-12
 */
public class EntCreditManger {

    private final Map<String, Company> RUNNING_CACHE_COMPANY = new HashMap<>();

    private final Map<String, Person> RUNNING_CACHE_PERSON = new HashMap<>();

    private final EntCreditBeanUnit beanUnit;

    public EntCreditManger(EntCreditBeanUnit beanUnit) {
        this.beanUnit = beanUnit;
    }

    /**
     * 资金链
     *
     * @param companyNames
     */
    public void pullFinancialChain(String[] companyNames) throws Exception {
        printInfo("=====================BEGIN=====================");
        Set<String> companyNameSet = new LinkedHashSet<>(Arrays.asList(companyNames));
        String companyId;
        for (String companyName : companyNameSet) {
            if (StringUtils.isBlank(companyName) || companyName.trim().length() < 3) {
                continue;
            }
            companyName = companyName.trim();
            companyId = null;
            // 公司信息
            Company companySaved = beanUnit.getRepository().selectCompanyByName(companyName);
            if (companySaved != null) {
                companyId = companySaved.getCompId();
                companyName = companySaved.getEntName();
            }
            if (StringUtils.isBlank(companyId)) {
                CompanyMiss companyMiss = beanUnit.getRepository().selectCompanyMissByName(companyName);
                if (companyMiss != null) {
                    String compId = companyMiss.getComp_id();
                    String newName = companyMiss.getNew_name();
                    if (StringUtils.isNotBlank(compId)) {
                        companyId = compId;
                    }
                    if (StringUtils.isNotBlank(newName)) {
                        companyName = newName;
                    }
                }
            }
            if (StringUtils.isBlank(companyId)) {
                companyId = AiQiCha.findCompanyId(companyName);
                if (StringUtils.isBlank(companyId)) {
                    System.err.printf("没查到：%s%n", companyName);
                    beanUnit.getRepository().logCompanyMiss(companyName);
                    continue;
                }
            }
            beanUnit.sendCompanyToCollector(null, companyName, companyId);
        }
        // 恢复
        Map<String, LinkedHashSet<String>> currentCollector = beanUnit.getCollector();
        int currentLevel = 1;
        TreeMap<Integer, Map<String, LinkedHashSet<String>>> unFinishedCollectors = new TreeMap<>();
        List<PullLog> unFinishedList = beanUnit.getRepository().selectAllUnFinished();
        if (CollectionUtils.isNotEmpty(unFinishedList)) {
            for (PullLog pullLog : unFinishedList) {
                Integer level = pullLog.getLevel();
                Map<String, LinkedHashSet<String>> collector = unFinishedCollectors.computeIfAbsent(level, l -> {
                    Map<String, LinkedHashSet<String>> clt = new LinkedHashMap<>();
                    clt.put(COMPANY_LIST_KEY, new LinkedHashSet<>());
                    clt.put(PERSON_LIST_KEY, new LinkedHashSet<>());
                    return clt;
                });
                if (company.name().equals(pullLog.getType())) {
                    collector.get(COMPANY_LIST_KEY).add(pullLog.getSourceKey());
                }
                if (person.name().equals(pullLog.getType())) {
                    collector.get(PERSON_LIST_KEY).add(pullLog.getSourceKey());
                }
            }
            //
            Integer oldLevel = unFinishedCollectors.firstKey();
            if (oldLevel > currentLevel) {
                currentCollector.clear();
                currentCollector = unFinishedCollectors.firstEntry().getValue();
            }
        }
        // 去重
        beanUnit.getRepository().selectAllCompany().forEach(c -> RUNNING_CACHE_COMPANY.put(c.getCompId(), c));
        beanUnit.getRepository().selectAllPerson().forEach(p -> RUNNING_CACHE_PERSON.put(p.getPersonId(), p));
        // 5层资金链
        while (!currentCollector.isEmpty() && currentLevel <= 5) {
            try {
                HttpApiRequestContext context = HttpApiRequestContext.getCurrent();
                context.setLevel(currentLevel);
                context.setBeanUnit(beanUnit);
                currentCollector = collectorExtract(currentCollector, currentLevel);
            } finally {
                HttpApiRequestContext.clean();
            }
            currentLevel++;
            Map<String, LinkedHashSet<String>> oldUnFinished = unFinishedCollectors.get(currentLevel);
            if (oldUnFinished != null && !oldUnFinished.isEmpty()) {
                currentCollector.computeIfAbsent(COMPANY_LIST_KEY, c -> new LinkedHashSet<>())
                        .addAll(oldUnFinished.get(COMPANY_LIST_KEY));
                currentCollector.computeIfAbsent(PERSON_LIST_KEY, p -> new LinkedHashSet<>())
                        .addAll(oldUnFinished.get(PERSON_LIST_KEY));
            }
        }
        printInfo("======================END======================");
    }

    private Map<String, LinkedHashSet<String>> collectorExtract(Map<String, LinkedHashSet<String>> collector,
                                                                int level) throws Exception {
        if (collector == null || collector.isEmpty()) {
            return collector;
        }
        if (CollectionUtils.isEmpty(collector.get(COMPANY_LIST_KEY))
                && CollectionUtils.isEmpty(collector.get(PERSON_LIST_KEY))) {
            return collector;
        }
        // 拉取公司信息
        LinkedHashSet<String> companyKeyList = collector.get(COMPANY_LIST_KEY);
        if (CollectionUtils.isNotEmpty(companyKeyList)) {
            String[] compInfo;
            List<String> levelCompanyKeys = new ArrayList<>(companyKeyList);
            for (String companyKey : levelCompanyKeys) {
                compInfo = companyKey.split(ID_DELIMITER);
                processCompanyDetail(compInfo[0], compInfo[1], level);
                beanUnit.getRepository().cleanExecFailed();
            }
            printInfo("完成第%s层：【公司->人员->", level);
            beanUnit.getCollector().get(COMPANY_LIST_KEY).removeAll(levelCompanyKeys);
        }
        // 拉取公司人员信息
        LinkedHashSet<String> personKeyList = collector.get(PERSON_LIST_KEY);
        if (CollectionUtils.isNotEmpty(personKeyList)) {
            String[] peopleInfo;
            List<String> levelPersonKeys = new ArrayList<>(personKeyList);
            for (String peopleKey : levelPersonKeys) {
                peopleInfo = peopleKey.split(ID_DELIMITER);
                processPersonDetail(peopleInfo[0], peopleInfo[1], level);
                beanUnit.getRepository().cleanExecFailed();
            }
            printInfo("完成第%s层：->人员->公司】", level);
            beanUnit.getCollector().get(PERSON_LIST_KEY).removeAll(levelPersonKeys);
        }
        return beanUnit.getCollector();
    }

    private void processCompanyDetail(String compName, String compId, int level) throws Exception {
        Company param = new Company();
        param.setEntName(compName);
        param.setCompId(compId);
        String logId = param.errId();
        if (RUNNING_CACHE_COMPANY.get(logId) != null) {
            return;
        }
        RUNNING_CACHE_COMPANY.put(logId, param);
        Company company = beanUnit.getReadService().getCompanyDetail(compName, compId);
        if (company != null && company.O_K()) {
            RUNNING_CACHE_COMPANY.put(logId, company);
            return;
        }
        // LOG
        PullLog pullLog = new PullLog();
        pullLog.setSourceKey(logId);
        pullLog.setType(SourceType.company.name());
        pullLog.setFinished(false);
        pullLog.setLevel(level);
        pullLog.setBegin_time(LocalDateTime.now());
        beanUnit.getRepository().savePullLog(pullLog);
        //
        company = AQC_BACKUP_0805.getReadService().getCompanyDetail(compName, compId);
        if (company == null || !company.O_K()) {
            company = beanUnit.getHttpReadService().getCompanyDetail(compName, compId);
        }
        if (company == null || !company.O_K()) {
            beanUnit.getRecorder().logCompanyErr(param, "processCompanyDetail > getCompanyDetail",
                    "未能获得公司信息");
            return;
        }
        Company companyDetail = company;
        beanUnit.getTxTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                beanUnit.getWriteService().saveCompany(companyDetail);
                beanUnit.getWriteService().saveCompanyStock(companyDetail.getStockList());
                beanUnit.getWriteService().saveCompanyInvest(companyDetail.getInvestList());
                beanUnit.getWriteService().saveCompanyKeyPerson(companyDetail.getKeyPersonList());
                beanUnit.getWriteService().saveCompanyHolds(companyDetail.getHoldsList());
                beanUnit.getWriteService().saveCompanyIndirectHolds(companyDetail.getIndirectHoldsList());
                beanUnit.getWriteService().saveCompanyChangeRecord(companyDetail.getChangeRecordList());
                beanUnit.getRepository().delCompanyExecFailed(companyDetail.getCompId());
                beanUnit.getRepository().setPullFinished(companyDetail.errId(), SourceType.company);
            }
        });
    }

    private void processPersonDetail(String personName, String personId, int level) throws Exception {
        Person param = new Person();
        param.setPersonName(personName);
        param.setPersonId(personId);
        String logId = param.errId();
        if (RUNNING_CACHE_PERSON.get(logId) != null) {
            return;
        }
        RUNNING_CACHE_PERSON.put(logId, param);
        Person person = beanUnit.getReadService().getPersonDetail(personName, personId);
        if (person != null && person.O_K()) {
            RUNNING_CACHE_PERSON.put(logId, person);
            return;
        }
        // LOG
        PullLog pullLog = new PullLog();
        pullLog.setSourceKey(logId);
        pullLog.setType(SourceType.person.name());
        pullLog.setFinished(false);
        pullLog.setBegin_time(LocalDateTime.now());
        pullLog.setLevel(level);
        beanUnit.getRepository().savePullLog(pullLog);
        //
        person = AQC_BACKUP_0805.getReadService().getPersonDetail(personName, personId);
        if (person == null || !person.O_K()) {
            person = beanUnit.getHttpReadService().getPersonDetail(personName, personId);
        }
        if (person == null || !person.O_K()) {
            beanUnit.getRecorder().logPersonErr(param, "processPersonDetail > getPersonDetail",
                    "未能获得人员信息");
            return;
        }
        Person personDetail = person;
        beanUnit.getTxTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                beanUnit.getWriteService().savePerson(personDetail);
                beanUnit.getWriteService().savePersonCompanies(personDetail.getCompanies());
                beanUnit.getRepository().delPersonExecFailed(personDetail.getPersonId());
                beanUnit.getRepository().setPullFinished(personDetail.errId(), SourceType.person);
            }
        });
    }

    private static void printInfo(String template, Object... args) {
        System.out.println("INFO -->> " + String.format(template, args));
    }
}
