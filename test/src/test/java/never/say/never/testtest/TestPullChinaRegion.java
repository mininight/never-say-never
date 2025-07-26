/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.testtest;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zaxxer.hikari.HikariDataSource;
import never.say.never.demo.ent_credit.api.ChinaRegionApiClient;
import never.say.never.demo.ent_credit.api.HttpApiClient;
import never.say.never.demo.ent_credit.entity.ChinaRegion;
import never.say.never.demo.ent_credit.http.HttpApiClientRequestInterceptor;
import never.say.never.test.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-28
 */
public class TestPullChinaRegion {

    private static final HttpApiClientRequestInterceptor httpApiInterceptor = new HttpApiClientRequestInterceptor();
    private static final ChinaRegionApiClient httpApiClient = HttpApiClient.newClient(ChinaRegionApiClient.class,
            ChinaRegionApiClient.BASE_URL, httpApiInterceptor);
    private static HikariDataSource dataSource;
    private static NamedParameterJdbcTemplate jdbcTemplate;
    private static PlatformTransactionManager txManager;
    private static TransactionTemplate txTemplate;

    @Before
    public void prepare() {
        dataSource = new HikariDataSource();
        dataSource.setAutoCommit(true);
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/yzpt_below?useSSL=false&useUnicode=true&characterEncoding=UTF8&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true&useAffectedRows=true&zeroDateTimeBehavior=CONVERT_TO_NULL");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        txManager = new DataSourceTransactionManager(dataSource);
        txTemplate = new TransactionTemplate(txManager);
        txTemplate.setReadOnly(false);
    }

    @After
    public void destroy() throws Exception {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    public void testSync() {
        JSONObject shengQuXianInfos;
        JSONArray regionInfo;
        // 省
        List<ChinaRegion> shengJiRegionList = httpApiClient.getShengJiCodes();
        String code;
        // 市
        for (ChinaRegion shengJiRegion : shengJiRegionList) {
            code = shengJiRegion.getCode();
            // 获得省、市、区信息
            shengQuXianInfos = httpApiClient.getShengQuXianInfo(code);
            List<ChinaRegion> shiJiRegionList = httpApiClient.getShiJiCodes(shengJiRegion);
            if (shiJiRegionList == null) {
                continue;
            }
            // 区
            for (ChinaRegion shiJiRegion : shiJiRegionList) {
                // 注入省级市、市级区信息
                regionInfo = shengQuXianInfos.getJSONArray(shiJiRegion.getCode());
                if (CollectionUtil.isNotEmpty(regionInfo)) {
                    if (StringUtils.isBlank(shiJiRegion.getName())) {
                        shiJiRegion.setName(regionInfo.getString(0));
                    }
                    if (StringUtils.isBlank(shiJiRegion.getParent().getName())) {
                        shiJiRegion.getParent().setName(regionInfo.getString(7));
                    }
                }
                //
                List<ChinaRegion> quXianJiRegionList = httpApiClient.getQuXianJiCodes(shiJiRegion);
                if (quXianJiRegionList == null) {
                    continue;
                }
                // 注入省、市、区信息
                for (ChinaRegion quXianRegion : quXianJiRegionList) {
                    // 注入省级市、市级区信息
                    regionInfo = shengQuXianInfos.getJSONArray(quXianRegion.getCode());
                    if (CollectionUtil.isNotEmpty(regionInfo)) {
                        if (StringUtils.isBlank(quXianRegion.getName())) {
                            quXianRegion.setName(regionInfo.getString(0));
                        }
                        if (StringUtils.isBlank(quXianRegion.getParent().getName())) {
                            quXianRegion.getParent().setName(regionInfo.getString(8));
                        }
                        if (StringUtils.isBlank(quXianRegion.getParent().getParent().getName())) {
                            quXianRegion.getParent().getParent().setName(regionInfo.getString(7));
                        }
                    }
                }
            }
        }
        List<ChinaRegion> allRegions = new ArrayList<>(shengJiRegionList);
        for (ChinaRegion sheng : shengJiRegionList) {
            List<ChinaRegion> shiList = sheng.getSubList();
            if (CollectionUtil.isEmpty(shiList)) {
                continue;
            }
            allRegions.addAll(shiList);
            for (ChinaRegion shi : shiList) {
                List<ChinaRegion> quList = shi.getSubList();
                if (CollectionUtil.isEmpty(quList)) {
                    continue;
                }
                allRegions.addAll(quList);
            }
        }
        saveRegions(allRegions);
    }

    private void saveRegions(List<ChinaRegion> regions) {
        if (CollectionUtil.isEmpty(regions)) {
            return;
        }
        try {
            BeanPropertySqlParameterSource[] parameterSources = regions.stream()
                    .map(BeanPropertySqlParameterSource::new)
                    .toArray(BeanPropertySqlParameterSource[]::new);
            jdbcTemplate.batchUpdate("insert into china_region (code, p_code, name, type)" +
                            " values " +
                            "(:code, :p_code, :name, :type) on duplicate key update " +
                            " name = CASE WHEN name is null then :name else name end",
                    parameterSources);
        } catch (Exception e) {
            throw e;
        }
    }
}
