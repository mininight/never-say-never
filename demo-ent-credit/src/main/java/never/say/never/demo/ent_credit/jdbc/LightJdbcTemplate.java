/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.jdbc;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Preconditions;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-15
 */
public class LightJdbcTemplate extends NamedParameterJdbcTemplate {

    public LightJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    public LightJdbcTemplate(JdbcOperations classicJdbcTemplate) {
        super(classicJdbcTemplate);
    }

    public <T> T selectOne(String sql, Map<String, ?> paramMap, Class<T> tClass) throws DataAccessException {
        JSONObject result = selectOne(sql, paramMap);
        if (result == null) {
            return null;
        }
        return result.toJavaObject(tClass);
    }

    public JSONObject selectOne(String sql, Map<String, ?> paramMap) throws DataAccessException {
        List<JSONObject> list = selectList(sql, paramMap);
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (list.size() != 1) {
            throw new IncorrectResultSizeDataAccessException(1, list.size());
        }
        return list.get(0);
    }

    public <T> List<T> selectList(String sql, Map<String, ?> paramMap, Class<T> tClass) throws DataAccessException {
        List<JSONObject> records = selectList(sql, paramMap);
        if (records == null) {
            return null;
        }
        if (records.isEmpty()) {
            return new ArrayList<>();
        }
        return records.stream().map(jsonObject -> jsonObject.toJavaObject(tClass)).collect(Collectors.toList());
    }

    public List<JSONObject> selectList(String sql, Map<String, ?> paramMap) throws DataAccessException {
        return query(sql, paramMap, JsonRowMapper.INSTANCE);
    }

    public int update(String sql, Object entity) throws DataAccessException {
        Preconditions.checkArgument(!BeanUtils.isSimpleValueType(entity.getClass()),
                "只接受实体或Map对象");
        SqlParameterSource source;
        if (entity instanceof Map) {
            source = new MapSqlParameterSource((Map<String, ?>) entity);
        } else {
            source = new BeanPropertySqlParameterSource(entity);
        }
        return update(sql, source);
    }
}
