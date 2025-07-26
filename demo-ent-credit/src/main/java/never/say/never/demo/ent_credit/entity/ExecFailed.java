/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.entity;

import lombok.Data;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-04
 */
@Data
public class ExecFailed {

    private String id;
    private String type;
    private String method;
    private Integer level;
    private String create_time;
    private String association_reqeust;
    private String err_msg;

    @Override
    public String toString() {
        return "\nExecFailed{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", method='" + method + '\'' +
                ", level=" + level +
                ", create_time='" + create_time + '\'' +
                ", \nassociation_reqeust='" + association_reqeust + '\'' +
                ", \nerr_msg='" + err_msg + '\'' +
                '}';
    }
}
