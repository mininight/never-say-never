/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.repository.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.f4b6a3.ulid.UlidCreator;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.enums.SourceChannel;
import never.say.never.demo.ent_credit.enums.SourceType;
import never.say.never.demo.ent_credit.jdbc.LightJdbcTemplate;
import never.say.never.demo.ent_credit.repository.EntCreditRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static never.say.never.demo.ent_credit.enums.SourceChannel.ID_DELIMITER;
import static never.say.never.demo.ent_credit.jdbc.JdbcTemplateMethod.*;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-04
 */
public class EntCreditJdbcRepository implements EntCreditRepository {

    private final LightJdbcTemplate jdbcTemplate;

    private final TransactionTemplate txTemplate;

    public EntCreditJdbcRepository(LightJdbcTemplate jdbcTemplate, TransactionTemplate txTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.txTemplate = txTemplate;
    }

    @Override
    public SourceId lookupId(SourceId sourceId) {
        SourceChannel channel = SourceChannel.of(sourceId.getChannel());
        SourceType type = SourceType.of(sourceId.getType());
        Preconditions.checkArgument(channel != null, "未建立的数据渠道：%s", sourceId.getChannel());
        Preconditions.checkArgument(type != null, "未建立的数据类型：%s", sourceId.getType());
        Map<String, ?> lookup = BeanMap.create(sourceId);
        List<SourceId> sourceIdList = null;
        switch (type) {
            case person:
                if (StringUtils.isNotBlank(sourceId.getName())) {
                    sourceIdList = jdbcTemplate.selectList(EntCredit_QueryPanel_selectSourceIdsByNameAndType.sql(),
                            lookup, SourceId.class);
                    sourceIdList = sourceIdList.stream().filter(source -> source.matchPerson(sourceId))
                            .collect(Collectors.toList());
                } else if (StringUtils.isNotBlank(sourceId.getValue())) {
                    sourceIdList = jdbcTemplate.selectList(EntCredit_QueryPanel_selectSourceIdsByValueAndType.sql(),
                            lookup, SourceId.class);
                } else {
                    throw new IllegalArgumentException("人员名称和源ID缺失");
                }
                break;
            case company:
                if (StringUtils.isNotBlank(sourceId.getName())) {
                    sourceIdList = jdbcTemplate.selectList(EntCredit_QueryPanel_selectSourceIdsByNameAndType.sql(),
                            lookup, SourceId.class);
                } else if (StringUtils.isNotBlank(sourceId.getValue())) {
                    sourceIdList = jdbcTemplate.selectList(EntCredit_QueryPanel_selectSourceIdsByValueAndType.sql(),
                            lookup, SourceId.class);
                } else {
                    throw new IllegalArgumentException("公司名称和源ID缺失");
                }
                break;
            default:
                break;
        }
        if (CollectionUtils.isNotEmpty(sourceIdList)) {
            sourceId.setId(sourceIdList.get(0).getId());
        } else {
            sourceId.setId(UlidCreator.getMonotonicUlid().toString());
        }
        lookup = BeanMap.create(sourceId);
        SourceId exist = jdbcTemplate.selectOne(EntCredit_QueryPanel_selectSourceIdsByIdAndChannelAndType.sql(),
                lookup, SourceId.class);
        if (exist != null) {
            if (SourceType.person == type) {
                String sign = exist.getSign();
                exist.matchPerson(sourceId);
                String signNew = exist.getSign();
                if (sign.length() == signNew.length()) {
                    return exist;
                }
            } else {
                return exist;
            }
        }
        if (StringUtils.isBlank(sourceId.getSign()) && sourceId.getPersonSign() != null) {
            sourceId.setSign(JSON.toJSONString(sourceId.getPersonSign()));
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(sourceId.getName()), "数据对象名称缺失");
        Preconditions.checkArgument(StringUtils.isNotBlank(sourceId.getValue()), "数据对象值缺失");
        jdbcTemplate.update(EntCredit_saveSourceId.sql(), sourceId);
        return sourceId;
    }

    @Override
    public List<Company> selectAllCompany() {
        List<Map<String, Object>> records = jdbcTemplate.queryForList("select * from company", new HashMap<>());
        if (CollectionUtils.isNotEmpty(records)) {
            return records.stream().map(r -> new JSONObject(r).toJavaObject(Company.class)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Person> selectAllPerson() {
        List<Map<String, Object>> records = jdbcTemplate.queryForList("select * from person", new HashMap<>());
        if (CollectionUtils.isNotEmpty(records)) {
            return records.stream().map(r -> new JSONObject(r).toJavaObject(Person.class)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void saveCompany(Company company) {
        Preconditions.checkArgument(company.O_K(), "无效的公司数据");
        jdbcTemplate.update(EntCredit_saveCompany.sql(), beanToSqlParamSource(company));
    }

    @Override
    public void saveCompanyStock(List<CompanyStock> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        jdbcTemplate.batchUpdate(EntCredit_saveCompanyStock.sql(), beanListToSqlParamSourceArray(list));
    }

    @Override
    public void saveCompanyKeyPerson(List<CompanyKeyPerson> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        jdbcTemplate.batchUpdate(EntCredit_saveCompanyKeyPerson.sql(), beanListToSqlParamSourceArray(list));
    }

    @Override
    public void saveCompanyChangeRecord(List<CompanyChangeRecord> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        jdbcTemplate.batchUpdate(EntCredit_saveCompanyChangeRecord.sql(), beanListToSqlParamSourceArray(list));
    }

    @Override
    public void saveCompanyInvest(List<CompanyInvest> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        jdbcTemplate.batchUpdate(EntCredit_saveCompanyInvest.sql(), beanListToSqlParamSourceArray(list));
    }

    @Override
    public void saveCompanyHolds(List<CompanyHolds> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        jdbcTemplate.batchUpdate(EntCredit_saveCompanyHolds.sql(), beanListToSqlParamSourceArray(list));
    }

    @Override
    public void saveCompanyIndirectHolds(List<CompanyIndirectHolds> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        jdbcTemplate.batchUpdate(EntCredit_saveCompanyIndirectHolds.sql(), beanListToSqlParamSourceArray(list));
    }

    @Override
    public Company selectCompanyById(String compId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(compId), "参数无效");
        List<Map<String, Object>> records = jdbcTemplate.queryForList(
                "select * from company where compId = :compId",
                ImmutableMap.of("compId", compId));
        if (CollectionUtils.isNotEmpty(records) && !records.isEmpty()) {
            return new JSONObject(records.get(0)).toJavaObject(Company.class);
        }
        return null;
    }

    @Override
    public Company selectCompanyByName(String entName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(entName), "参数无效");
        return jdbcTemplate.selectOne(
                EntCredit_QueryPanel_selectCompanyByName.sql(),
                ImmutableMap.of("entName", entName),
                Company.class
        );
    }

    @Override
    public List<CompanyStock> selectCompanyStock(String id) {
        Preconditions.checkArgument(StringUtils.isNotBlank(id), "参数无效");
        return jdbcTemplate.selectList(
                EntCredit_QueryPanel_selectCompanyStock.sql(),
                ImmutableMap.of("id", id),
                CompanyStock.class
        );
    }

    @Override
    public List<CompanyInvest> selectCompanyInvest(String id) {
        Preconditions.checkArgument(StringUtils.isNotBlank(id), "参数无效");
        return jdbcTemplate.selectList(
                EntCredit_QueryPanel_selectCompanyInvest.sql(),
                ImmutableMap.of("id", id),
                CompanyInvest.class
        );
    }

    @Override
    public List<CompanyKeyPerson> selectCompanyKeyPerson(String id) {
        Preconditions.checkArgument(StringUtils.isNotBlank(id), "参数无效");
        return jdbcTemplate.selectList(
                EntCredit_QueryPanel_selectCompanyKeyPerson.sql(),
                ImmutableMap.of("id", id),
                CompanyKeyPerson.class
        );
    }

    @Override
    public List<CompanyHolds> selectCompanyHolds(String id) {
        Preconditions.checkArgument(StringUtils.isNotBlank(id), "参数无效");
        return jdbcTemplate.selectList(
                EntCredit_QueryPanel_selectCompanyHolds.sql(),
                ImmutableMap.of("id", id),
                CompanyHolds.class
        );
    }

    @Override
    public List<CompanyIndirectHolds> selectCompanyIndirectHolds(String id) {
        Preconditions.checkArgument(StringUtils.isNotBlank(id), "参数无效");
        return jdbcTemplate.selectList(
                EntCredit_QueryPanel_selectCompanyIndirectHolds.sql(),
                ImmutableMap.of("id", id),
                CompanyIndirectHolds.class
        );
    }

    @Override
    public List<CompanyChangeRecord> selectCompanyChangeRecord(String id) {
        Preconditions.checkArgument(StringUtils.isNotBlank(id), "参数无效");
        return jdbcTemplate.selectList(
                EntCredit_QueryPanel_selectCompanyChangeRecord.sql(),
                ImmutableMap.of("id", id),
                CompanyChangeRecord.class
        );
    }

    @Override
    public Person selectPerson(String personId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(personId), "参数无效");
        List<Map<String, Object>> records = jdbcTemplate.queryForList(
                "select * from person where personId = :personId",
                ImmutableMap.of("personId", personId));
        if (CollectionUtils.isNotEmpty(records) && !records.get(0).isEmpty()) {
            Person person = new JSONObject(records.get(0)).toJavaObject(Person.class);
            if (StringUtils.isNotBlank(person.getJson_str())) {
                return JSON.parseObject(person.getJson_str(), Person.class);
            } else {
                return person;
            }
        }
        return null;
    }

    @Override
    public List<PersonCompany> selectPersonCompanyList(String personId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(personId), "参数无效");
        return jdbcTemplate.selectList(EntCredit_QueryPanel_selectPersonCompanyList.sql(),
                ImmutableMap.of("personId", personId),
                PersonCompany.class
        );
    }

    @Override
    public void savePerson(Person person) {
        Preconditions.checkArgument(person.O_K(), "无效的人员数据");
        Person old = selectPerson(person.getPersonId());
        if (old != null && StringUtils.isNotBlank(old.getJson_str()) && StringUtils.isBlank(person.getJson_str())) {
            return;
        }
        if (old != null && Objects.equals(old.getJson_str(), person.getJson_str())) {
            return;
        }
        jdbcTemplate.update(EntCredit_savePerson.sql(), beanToSqlParamSource(person));
    }

    @Override
    public void savePersonCompany(List<PersonCompany> personCompanyList) {
        jdbcTemplate.batchUpdate(EntCredit_savePersonCompany.sql(), beanListToSqlParamSourceArray(personCompanyList));
    }

    @Override
    public List<PersonCompany> selectPersonCompanyListByLevel(int level) {
        List<Map<String, Object>> records = jdbcTemplate.queryForList(
                "select * from person_company where level=:level",
                ImmutableMap.of("level", level)
        );
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(records);
        return jsonArray.toJavaList(PersonCompany.class);
    }

    @Override
    public Integer selectMaxLevel() {
        Map<String, Object> maxLevel = jdbcTemplate.queryForMap(
                "select max(level) as maxLevel from person_company",
                new HashMap<>()
        );
        if (!maxLevel.isEmpty()) {
            return (Integer) maxLevel.get("maxLevel");
        }
        return null;
    }

    @Override
    public CompanyMiss selectCompanyMissByName(String companyName) {
        List<Map<String, Object>> records = jdbcTemplate.queryForList(
                "select * from company_miss where name=:companyName or new_name=:companyName",
                ImmutableMap.of("companyName", companyName)
        );
        if (CollectionUtils.isNotEmpty(records) && !records.isEmpty()) {
            return new JSONObject(records.get(0)).toJavaObject(CompanyMiss.class);
        }
        return null;
    }

    @Override
    public void logCompanyMiss(String companyName) {
        jdbcTemplate.update("insert ignore into company_miss (name) values (:name)",
                ImmutableMap.of("name", companyName));
    }

    @Override
    public void logExecFailed(ExecFailed execFailed) {
        jdbcTemplate.update(EntCredit_saveExecFailed.sql(), beanToSqlParamSource(execFailed));
    }

    @Override
    public void delPersonExecFailed(String id) {
        jdbcTemplate.update("delete from exec_failed where id = :id and type=:type",
                ImmutableMap.of("id", id, "type", "person"));
    }

    @Override
    public void delCompanyExecFailed(String id) {
        jdbcTemplate.update("delete from exec_failed where id = :id and type=:type",
                ImmutableMap.of("id", id, "type", "company"));
    }

    private PullLog selectPullLogById(String id) {
        List<Map<String, Object>> records = jdbcTemplate.queryForList("select * from pull_log where sourceKey =:id",
                ImmutableMap.of("id", id));
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return new JSONObject(records.get(0)).toJavaObject(PullLog.class);
    }

    @Override
    public void savePullLog(PullLog pullLog) {
        String id = pullLog.getSourceKey();
        Preconditions.checkArgument(id.contains(ID_DELIMITER), "日志ID格式错误");
        if (selectPullLogById(id) == null) {
            jdbcTemplate.update(EntCredit_savePullLog.sql(), new BeanPropertySqlParameterSource(pullLog));
        }
    }

    @Override
    public void setPullFinished(String id, SourceType sourceType) {
        Preconditions.checkArgument(!id.contains(ID_DELIMITER), "日志ID格式错误");
        jdbcTemplate.update(
                "update pull_log set finished=1, end_time=:endTime where sourceKey =:id",
                ImmutableMap.of(
                        "id", id,
                        "endTime", LocalDateTime.now()
                ));
    }

    @Override
    public void savePullLogSub(PullLogSub pullLogSub) {
        String id = pullLogSub.getSourceKey();
        String pid = pullLogSub.getParentKey();
        Preconditions.checkArgument(!id.contains(ID_DELIMITER), "SUB日志ID格式错误");
        if (StringUtils.isNotBlank(pid)) {
            Preconditions.checkArgument(!pid.contains(ID_DELIMITER), "SUB日志PID格式错误");
        }
        jdbcTemplate.update(EntCredit_savePullLogSub.sql(), new BeanPropertySqlParameterSource(pullLogSub));
    }

    @Override
    public List<PullLog> selectAllUnFinished() {
        List<Map<String, Object>> records = jdbcTemplate.queryForList(
                "select * from pull_log where finished = 0 order by level",
                ImmutableMap.of()
        );
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        return records.stream().map(JSONObject::new).map(obj -> obj.toJavaObject(PullLog.class)).collect(Collectors.toList());
    }

    @Override
    public List<PullLog> selectAllFinished() {
        List<Map<String, Object>> records = jdbcTemplate.queryForList(
                "select * from pull_log where finished = 1 order by level",
                ImmutableMap.of()
        );
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        return records.stream().map(JSONObject::new).map(obj -> obj.toJavaObject(PullLog.class)).collect(Collectors.toList());
    }

    @Override
    public void cleanExecFailed() {
        jdbcTemplate.getJdbcTemplate().execute("call clean_exec_failed()",
                (CallableStatementCallback<Object>) cs -> null);
    }

    private SqlParameterSource beanToSqlParamSource(Object object) {
        if (object == null || BeanUtils.isSimpleValueType(object.getClass())) {
            return null;
        }
        if (object instanceof Map) {
            return new MapSqlParameterSource((Map<String, ?>) object);
        }
        return new BeanPropertySqlParameterSource(object);
    }

    private SqlParameterSource[] beanListToSqlParamSourceArray(List<?> beans) {
        List<SqlParameterSource> parameterSources = new ArrayList<>();
        SqlParameterSource sqlParameterSource;
        for (Object object : beans) {
            sqlParameterSource = beanToSqlParamSource(object);
            if (sqlParameterSource == null) {
                continue;
            }
            parameterSources.add(sqlParameterSource);
        }
        return parameterSources.toArray(new SqlParameterSource[0]);
    }
}
