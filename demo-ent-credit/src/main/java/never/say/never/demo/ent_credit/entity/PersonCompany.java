/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;

/**
 * 人名下公司
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-29
 */
@HeadRowHeight(36)
@ColumnWidth(15)
@Data
public class PersonCompany {
    @ExcelIgnore
    private String personId;
    @ExcelIgnore
    private String compId;
    @ExcelProperty("人员")
    @ColumnWidth(12)
    private String personName;
    @ColumnWidth(50)
    @ExcelProperty("公司")
    private String compName;
    @ExcelIgnore
    private String preCompName;
    @ExcelProperty("公司品牌")
    private String logoWord;
    @ColumnWidth(25)
    @ExcelProperty("统一社会信用代码")
    private String unifiedCode;
    @ExcelIgnore
    private String regNo;
    @ExcelIgnore
    private String region;
    @ColumnWidth(28)
    @ExcelProperty("公司地址")
    private String regAddr;
    @ExcelProperty("公司状态")
    private String openStatus;
    @ColumnWidth(25)
    @ExcelProperty("和公司的关系")
    private String relationShip;
    @ExcelProperty("成立日期")
    private String startDate;
    @ExcelIgnore
    private String regCap;
    @ExcelProperty("公司电话")
    private String telephone;
    @ExcelProperty("公司邮箱")
    private String mailbox;
    @ExcelProperty("公司账号")
    private String bankInfo;
    @ExcelIgnore
    private Integer level;

    @ExcelIgnore
    private Person person;
    @ExcelIgnore
    private Company company;

    public static PersonCompany of(Person person, Company company, String personRole) {
        PersonCompany personCompany = new PersonCompany();
        personCompany.setPerson(person);
        personCompany.setCompany(company);
        personCompany.setPersonId(person.getPersonId());
        personCompany.setPersonName(person.getPersonName());
        personCompany.setCompId(company.getCompId());
        personCompany.setCompName(company.getEntName());
        personCompany.setPreCompName(company.getPreEntName());
        personCompany.setLogoWord(company.getEntLogoWord());
        personCompany.setUnifiedCode(company.getUnifiedCode());
        personCompany.setRegNo(company.getRegNo());
        personCompany.setRegAddr(company.getRegAddr());
        personCompany.setRegion(company.getRegAddr());
        personCompany.setRegCap(company.getRegCapital());
        personCompany.setOpenStatus(company.getOpenStatus());
        personCompany.setStartDate(company.getStartDate());
        personCompany.setBankInfo(company.getBankInfo());
        personCompany.setMailbox(company.getEmail());
        personCompany.setTelephone(company.getTelephone());
        personCompany.setLevel(HttpApiRequestContext.getCurrent().getLevel());
        personCompany.setRelationShip(personRole);
        return personCompany;
    }
}
