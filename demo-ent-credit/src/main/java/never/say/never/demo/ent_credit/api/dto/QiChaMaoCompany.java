/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api.dto;

import lombok.Data;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-11
 */
@Data
public class QiChaMaoCompany {
    private String 名称;
    private String 曾用名;
    private String 注册资本;
    private String 企业地址;
    private String 纳税人识别号;
    private String 经营状态;
    private String 经营范围;
    private String 社会信用代码查询;
    private String 经营期限;
    private String 所属地区;
    private String 商标名查询;
    private String 营业执照查询;
    private String 法定代表人;
    private String 注册号;
    private String 机构代码;
    private String 企业类型;
    private String 核准日期;
    private String 成立日期;
    private String 登记机关;
    private String 统一社会信用代码;
    //
    private String 所属行业;
    private String 前瞻标签;
    private String 业务标签;
    private String 展会标签;
    private String 经营范围关键词;

    public String industry() {
        return String.join("; ", 所属行业, 前瞻标签, 业务标签, 展会标签, 经营范围关键词);
    }
}
