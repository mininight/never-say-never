/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.enums;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-20
 */
@Getter
public enum PersonRole {

    /**
     * 法人
     */
    legal_person("法定代表人", "法人"),

    /**
     * 董事长
     */
    chairman("董事长"),

    /**
     * 董事
     */
    director("执行董事", "董事"),

    /**
     * 总经理
     */
    general_manager("总经理"),

    /**
     * 经理
     */
    manager("经理"),

    /**
     * 财务
     */
    finance("财务负责人", "财务"),

    /**
     * 监事
     */
    supervisor("监事"),

    /**
     * 股东
     */
    shareholder("股东"),

    /**
     * 间接持股
     */
    indirect_holder("间接持股"),

    /**
     * 投资控股
     */
    invest_control("投资", "控股"),

    /**
     * 关键人
     */
    key_person("主要", "关键", "负责人", "最终受益", "控制", "高管"),

    ;

    private final List<String> keywords = new ArrayList<>();


    PersonRole(String... keywords) {
        Collections.addAll(this.keywords, keywords);
    }

    public static Set<PersonRole> extract(String roleText) {
        Preconditions.checkArgument(StringUtils.isNotBlank(roleText), "空参");
        roleText = roleText.trim();
        Preconditions.checkArgument(!"-".equals(roleText), "空参");
        String rt = roleText;
        Set<PersonRole> result = Arrays.stream(values()).filter(r -> {
            for (String kw : r.getKeywords()) {
                if (Objects.equals(rt, kw) || rt.contains(kw)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toSet());
        if (result.contains(chairman)) {
            result.remove(director);
        }
        if (result.contains(general_manager)) {
            result.remove(manager);
        }
        return result;
    }
}
