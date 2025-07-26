/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.configure;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import never.say.never.demo.ent_credit.api.HttpEntCreditApi;
import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.enums.SourceChannel;
import never.say.never.demo.ent_credit.enums.SourceType;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.http.HttpApiSLBPoints;
import never.say.never.demo.ent_credit.jdbc.LightJdbcTemplate;
import never.say.never.demo.ent_credit.logger.ExecFailedRecorder;
import never.say.never.demo.ent_credit.manger.EntCreditManger;
import never.say.never.demo.ent_credit.repository.EntCreditRepository;
import never.say.never.demo.ent_credit.repository.impl.EntCreditJdbcRepository;
import never.say.never.demo.ent_credit.service.EntCreditHttpReadService;
import never.say.never.demo.ent_credit.service.EntCreditReadService;
import never.say.never.demo.ent_credit.service.EntCreditWriteService;
import never.say.never.demo.ent_credit.service.impl.EntCreditHttpReadServiceImpl;
import never.say.never.demo.ent_credit.service.impl.EntCreditReadServiceImpl;
import never.say.never.demo.ent_credit.service.impl.EntCreditWriteServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static never.say.never.demo.ent_credit.enums.SourceChannel.ID_DELIMITER;
import static never.say.never.demo.ent_credit.enums.SourceType.company;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-13
 */
@Getter
public enum EntCreditBeanUnit implements BeanNameAware, ApplicationContextAware, InitializingBean, DisposableBean {

    /**
     * 下线
     */
    BELOW,

    /**
     * 上线
     */
    TOP,

    /**
     * 爱企查0805备份
     */
    AQC_BACKUP_0805(true),

    /**
     * 爱企查0818备份
     */
    AQC_BACKUP_0818(true),
    ;
    public static final String COMPANY_LIST_KEY = "COMPANY_LIST_KEY";
    public static final String PERSON_LIST_KEY = "PERSON_LIST_KEY";
    private static ApplicationContext APP_CTX;
    private static AutowireCapableBeanFactory autowireCapableBeanFactory;
    private static final Map<String, InitializingBean> initializingBeanMap = new LinkedHashMap<>();
    private static final Map<String, DisposableBean> disposableBeanMap = new LinkedHashMap<>();

    private final boolean readonly;
    private final Map<String, LinkedHashSet<String>> collector = new LinkedHashMap<>();
    private HikariDataSource dataSource;
    private LightJdbcTemplate jdbcTemplate;
    private DataSourceTransactionManager txManager;
    private TransactionTemplate txTemplate;
    private EntCreditRepository repository;
    private ExecFailedRecorder recorder;
    private EntCreditHttpReadService httpReadService;
    private EntCreditReadService readService;
    private EntCreditWriteService writeService;
    private EntCreditManger manger;
    private HttpApiSLBPoints httpApiSLBPoints;
    private String unitName;

    EntCreditBeanUnit() {
        this(false);
    }

    EntCreditBeanUnit(boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public void setBeanName(String name) {
        this.unitName = name;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        APP_CTX = ctx;
        autowireCapableBeanFactory = ctx.getAutowireCapableBeanFactory();
        EntCreditProperties entCreditProperties = APP_CTX.getBean(EntCreditProperties.class);
        DataSourceProperties dataSourceProperties = entCreditProperties.getDatasource().get(this);
        //
        dataSource = regBean(newDataSource(dataSourceProperties), "dataSource");
        jdbcTemplate = regBean(new LightJdbcTemplate(dataSource), "jdbcTemplate");
        txManager = regBean(new JdbcTransactionManager(dataSource), "txManager");
        txTemplate = regBean(new TransactionTemplate(txManager), "txTemplate");
        dataSource.setAutoCommit(true);
        dataSource.setReadOnly(readonly);
        txTemplate.setReadOnly(readonly);
        //
        repository = regBean(new EntCreditJdbcRepository(jdbcTemplate, txTemplate), "repository");
        recorder = regBean(new ExecFailedRecorder(repository), "recorder");
        httpReadService = regBean(new EntCreditHttpReadServiceImpl(this), "httpReadService");
        readService = regBean(new EntCreditReadServiceImpl(this), "readService");
        writeService = regBean(new EntCreditWriteServiceImpl(this), "writeService");
        manger = regBean(new EntCreditManger(this), "manger");
        //
        httpApiSLBPoints = new HttpApiSLBPoints();
        httpApiSLBPoints.setBeanUnit(this);
    }

    @Override
    public void destroy() throws Exception {
        dataSource.close();
        for (DisposableBean disposableBean : disposableBeanMap.values()) {
            disposableBean.destroy();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (InitializingBean initializingBean : initializingBeanMap.values()) {
            initializingBean.afterPropertiesSet();
        }
    }

    private <T> T regBean(T bean, String beanNameSuffix) {
        String beanName = unitName + "_" + beanNameSuffix;
        autowireCapableBeanFactory.initializeBean(bean, beanName);
        autowireCapableBeanFactory.autowireBean(bean);
        if (bean instanceof InitializingBean) {
            initializingBeanMap.put(beanName, (InitializingBean) bean);
        }
        if (bean instanceof DisposableBean) {
            disposableBeanMap.put(beanName, (DisposableBean) bean);
        }
        return bean;
    }

    private static HikariDataSource newDataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        return dataSource;
    }

    public Map<String, LinkedHashSet<String>> getCollector() {
        collector.computeIfAbsent(COMPANY_LIST_KEY, c -> new LinkedHashSet<>());
        collector.computeIfAbsent(PERSON_LIST_KEY, p -> new LinkedHashSet<>());
        return collector;
    }

    public void sendToCollector(String key, String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        LinkedHashSet<String> list = getCollector().computeIfAbsent(key, k -> new LinkedHashSet<>());
        if (list.contains(id)) {
            return;
        }
        list.add(id);
    }

    public void sendCompanyToCollector(String parentId, String companyName, String companyId) {
        if (StringUtils.isBlank(companyId)) {
            return;
        }
        if (companyName == null) {
            companyName = "";
        }
        Integer currentLevel = HttpApiRequestContext.getCurrent().getLevel();
        int level = StringUtils.isBlank(parentId) && currentLevel == null ? 1 : currentLevel + 1;
        SourceChannel channel = SourceChannel.of((HttpEntCreditApi) HttpApiRequestContext.getCurrent().getHttpApiClient());
        String channelName = channel == null ? null : channel.name();
        String logId = companyName + ID_DELIMITER + companyId;
        PullLog pullLog = new PullLog();
        pullLog.setSourceKey(logId);
        pullLog.setChannel(channelName);
        pullLog.setType(company.name());
        pullLog.setBegin_time(LocalDateTime.now());
        pullLog.setFinished(false);
        pullLog.setLevel(level);
        repository.savePullLog(pullLog);
        if (StringUtils.isNotBlank(parentId)) {
            PullLogSub pullLogSub = new PullLogSub();
            pullLogSub.setParentKey(parentId);
            pullLogSub.setSourceKey(logId);
            pullLogSub.setChannel(channelName);
            pullLogSub.setType(company.name());
            pullLogSub.setLevel(level);
            repository.savePullLogSub(pullLogSub);
        }
        sendToCollector(COMPANY_LIST_KEY, logId);
    }

    public void sendPersonToCollector(String parentId, Person person) {
        if (person == null || StringUtils.isBlank(person.getPersonId())) {
            return;
        }
        Integer currentLevel = HttpApiRequestContext.getCurrent().getLevel();
        int level = StringUtils.isBlank(parentId) && currentLevel == null ? 1 : currentLevel + 1;
        SourceChannel channel = SourceChannel.of((HttpEntCreditApi) HttpApiRequestContext.getCurrent().getHttpApiClient());
        String channelName = channel == null ? null : channel.name();
        String logId = person.errId();
        PullLog pullLog = new PullLog();
        pullLog.setSourceKey(logId);
        pullLog.setChannel(channelName);
        pullLog.setType(SourceType.person.name());
        pullLog.setBegin_time(LocalDateTime.now());
        pullLog.setFinished(false);
        pullLog.setLevel(level);
        repository.savePullLog(pullLog);
        if (StringUtils.isNotBlank(parentId)) {
            PullLogSub pullLogSub = new PullLogSub();
            pullLogSub.setParentKey(parentId);
            pullLogSub.setSourceKey(logId);
            pullLogSub.setChannel(channelName);
            pullLogSub.setType(SourceType.person.name());
            pullLogSub.setLevel(level);
            repository.savePullLogSub(pullLogSub);
        }
        sendToCollector(PERSON_LIST_KEY, person.getPersonName() + ID_DELIMITER + person.getPersonId());
    }

    public void sendCompanyStockToCollector(Company company, CompanyStock companyStock) {
        if (companyStock == null) {
            return;
        }
        String errId = company.errId();
        if (StringUtils.isBlank(companyStock.getPersonId())) {
            // 公司股东
            sendCompanyToCollector(errId, companyStock.getStockName(), companyStock.getStockId());
        } else {
            // 个人股东
            sendPersonToCollector(errId, companyStock);
        }
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return APP_CTX.getBean(requiredType);
    }

    public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return APP_CTX.getBean(name, requiredType);
    }

}
