/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.entity;

import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-04
 */
@Data
public class ChinaRegion {
    public static final String ROOT_CODE = "100000";

    private String code;
    private String p_code;
    private String name;
    private Integer type;
    private ChinaRegion parent;
    private List<ChinaRegion> subList;

    @Override
    public String toString() {
        return "ChinaRegion{" +
                "code='" + code + '\'' +
                ", pCode='" + p_code + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    @Getter
    public enum AreaType {
        SHENG(1),
        SHI(2),
        QU_XIAN(3),
        ;
        private final Integer value;

        AreaType(Integer value) {
            this.value = value;
        }
    }
}
