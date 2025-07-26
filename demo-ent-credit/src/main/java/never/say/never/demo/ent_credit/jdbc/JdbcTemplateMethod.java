/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.jdbc;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-29
 */
public enum JdbcTemplateMethod {

    /**
     * 保存数据来源ID
     */
    EntCredit_saveSourceId,

    /**
     * 通过数据对象值和类型获取数据来源ID
     */
    EntCredit_QueryPanel_selectSourceIdsByValueAndType,

    /**
     * 通过数据对象名称和类型获取数据来源ID
     */
    EntCredit_QueryPanel_selectSourceIdsByNameAndType,

    /**
     * 通过数据对象ID、渠道和类型获取数据来源ID
     */
    EntCredit_QueryPanel_selectSourceIdsByIdAndChannelAndType,

    /**
     * 保存公司及人员关系信息
     */
    EntCredit_savePersonCompany,

    /**
     * 保存公司信息
     */
    EntCredit_saveCompany,

    /**
     * 保存公司股东
     */
    EntCredit_saveCompanyStock,

    /**
     * 保存公司主要人员信息
     */
    EntCredit_saveCompanyKeyPerson,

    /**
     * 保存公司变更记录
     */
    EntCredit_saveCompanyChangeRecord,

    /**
     * 保存公司投资信息
     */
    EntCredit_saveCompanyInvest,

    /**
     * 保存公司持股信息
     */
    EntCredit_saveCompanyHolds,

    /**
     * 保存公司间接持股信息
     */
    EntCredit_saveCompanyIndirectHolds,

    /**
     * 保存人员信息
     */
    EntCredit_savePerson,

    /**
     * 错误日志
     */
    EntCredit_saveExecFailed,

    /**
     * 拉取日志
     */
    EntCredit_savePullLog,

    /**
     * 拉取日志
     */
    EntCredit_savePullLogSub,

    /**
     * 通过公司名称查询公司
     */
    EntCredit_QueryPanel_selectCompanyByName,

    /**
     * 查询公司股东
     */
    EntCredit_QueryPanel_selectCompanyStock,

    /**
     * 查询公司对外投资公司
     */
    EntCredit_QueryPanel_selectCompanyInvest,

    /**
     * 查询公司主要成员
     */
    EntCredit_QueryPanel_selectCompanyKeyPerson,

    /**
     * 查询公司控股公司
     */
    EntCredit_QueryPanel_selectCompanyHolds,

    /**
     * 查询公司间接持股公司
     */
    EntCredit_QueryPanel_selectCompanyIndirectHolds,

    /**
     * 查询公司变更记录
     */
    EntCredit_QueryPanel_selectCompanyChangeRecord,

    /**
     * 查询人员公司记录
     */
    EntCredit_QueryPanel_selectPersonCompanyList,

    ;
    private String sql;
    public static final String QUERY_PANEL_TOKEN = "QueryPanel_";

    JdbcTemplateMethod() {
        String name = name();
        String queryPanel = null;
        String queryPanelMethod = null;
        if (name.contains(QUERY_PANEL_TOKEN)) {
            String[] items = name.split("_");
            queryPanel = items[0] + "_" + items[1];
            queryPanelMethod = items[2];
        }
        String queryPanelSqlMethodName = queryPanelMethod;
        String sqlFile = "JDBC_Repository/" + (StringUtils.isNotBlank(queryPanelMethod) ? queryPanel : name) + ".sql";
        try {
            sql = StreamUtils.copyToString(new ClassPathResource(sqlFile).getInputStream(), Charset.defaultCharset());
            if (StringUtils.isNotBlank(queryPanelMethod)) {
                sql = Arrays.stream(sql.split("#"))
                        .filter(s -> StringUtils.isNotBlank(s) && s.contains(queryPanelSqlMethodName))
                        .findFirst().orElse(null);
                Preconditions.checkArgument(StringUtils.isNotBlank(sql), "没有发现 %s 的SQL脚本",
                        name);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String sql() {
        return sql;
    }
}
