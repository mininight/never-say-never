/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Data;
import never.say.never.demo.ent_credit.enums.PersonRole;
import never.say.never.demo.ent_credit.enums.SourceType;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-24
 */
@Data
public class SourceId {
    private static final TypeReference<Map<String, Set<PersonRole>>> PERSON_SIGN_TYPE = new TypeReference<>() {
    };

    private String id;
    private String name;
    private String channel;
    private String value;
    private String type;
    private String sign;
    private Map<String, Set<PersonRole>> personSign;

    public boolean complete() {
        boolean complete = StringUtils.isNotBlank(id)
                && StringUtils.isNotBlank(name)
                && StringUtils.isNotBlank(channel)
                && StringUtils.isNotBlank(value)
                && StringUtils.isNotBlank(type);
        if (SourceType.person.name().equals(type)) {
            return complete && StringUtils.isNotBlank(sign);
        }
        return complete;
    }

    public boolean matchPerson(SourceId sourceId) {
        Preconditions.checkArgument(SourceType.of(sourceId.getType()) == SourceType.person,
                "非人员的源数据");
        Preconditions.checkArgument(!sourceId.getPersonSign().isEmpty(), "人员缺少公司角色");
        Map<String, Set<PersonRole>> personSign = getPersonSign();
        Map<String, Set<PersonRole>> param = sourceId.getPersonSign();
        Map.Entry<String, Set<PersonRole>> personCompanyRoleItem = personSign.entrySet().stream()
                .filter(kv -> param.containsKey(kv.getKey()))
                .findFirst().orElse(null);
        if (personCompanyRoleItem == null) {
            return false;
        }
        String companyId = personCompanyRoleItem.getKey();
        Set<PersonRole> personRoleList = personCompanyRoleItem.getValue();
        Set<PersonRole> personRoles = param.get(companyId);
        boolean match = IterableUtils.matchesAny(personRoleList, personRoles::contains);
        if (!match) {
            return false;
        }
        boolean update = false;
        for (PersonRole role : personRoles) {
            if (!personRoleList.contains(role)) {
                personRoleList.add(role);
                update = true;
            }
        }
        for (String compId : param.keySet()) {
            if (!personSign.containsKey(compId)) {
                personSign.put(compId, param.get(compId));
                update = true;
            }
        }
        if (update) {
            this.personSign = personSign;
            sign = JSON.toJSONString(this.personSign);
        }
        return true;
    }

    public Map<String, Set<PersonRole>> getPersonSign() {
        if (personSign != null && !personSign.isEmpty()) {
            return personSign;
        }
        if (StringUtils.isBlank(sign)) {
            personSign = Maps.newHashMap();
        } else {
            personSign = JSON.parseObject(sign).toJavaObject(PERSON_SIGN_TYPE);
        }
        return personSign;
    }
}
