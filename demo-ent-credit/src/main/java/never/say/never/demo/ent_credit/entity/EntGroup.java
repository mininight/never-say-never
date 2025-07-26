/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-09-12
 */
@Data
public class EntGroup {
    private String id;
    private String name;
    private String dominantCompId;
    private String dominantCompName;
    private Company dominantCompany;
    private final Map<String, Company> relateCompanyMap = new HashMap<>();
    private final Map<String, Person> relatePersonMap = new HashMap<>();
}
