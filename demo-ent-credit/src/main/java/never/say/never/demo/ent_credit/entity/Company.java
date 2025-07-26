/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.entity;

import lombok.Data;
import never.say.never.demo.ent_credit.http.HttpApiSLB;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static never.say.never.demo.ent_credit.enums.SourceChannel.ID_DELIMITER;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-29
 */
@Data
public class Company implements HttpApiSLB.Res {
    private String compId;
    private String entName;
    private String preEntName;
    private String openStatus;
    private String unifiedCode;
    private String taxNo;
    private String orgNo;
    private String regCode;
    private String regNo;
    private String scope;
    private String regAddr;
    private String legalPerson;
    private String personId;
    private Integer legalCompNum;
    private String startDate;
    private String openTime;
    private String annualDate;
    private String regCapital;
    private String industry;
    private String district;
    private String authority;
    private String paidinCapital;
    private String entLogoWord;
    private String insurance;
    private String entType;
    private String bankInfo;
    private String telephone;
    private String realCapital;
    private String orgType;
    private String email;
    private String personTitle;
    private String licenseNumber;
    //
    private Integer level;
    private SourceId sourceId;
    private Person legalPersonInfo;
    private Company legalCompanyInfo;

    private List<CompanyStock> stockList;
    private List<CompanyHolds> holdsList;
    private List<CompanyInvest> investList;
    private List<CompanyIndirectHolds> indirectHoldsList;
    private List<CompanyKeyPerson> keyPersonList;
    private List<CompanyChangeRecord> changeRecordList;

    private final Map<String, Company> relateCompanyMap = new HashMap<>();
    private final Map<String, Person> relatePersonMap = new HashMap<>();

    public void setLevel(Integer level) {
        if (level == null) {
            return;
        }
        this.level = level;
    }

    public void setPreEntName(String preEntName) {
        if (StringUtils.isBlank(preEntName)) {
            return;
        }
        this.preEntName = preEntName;
    }

    public void setLegalCompNum(Integer legalCompNum) {
        if (legalCompNum == null) {
            return;
        }
        this.legalCompNum = legalCompNum;
    }

    public void setOpenStatus(String openStatus) {
        if (StringUtils.isBlank(openStatus)) {
            return;
        }
        this.openStatus = openStatus;
    }

    public void setUnifiedCode(String unifiedCode) {
        if (StringUtils.isBlank(unifiedCode)) {
            return;
        }
        this.unifiedCode = unifiedCode;
    }

    public void setRegCode(String regCode) {
        if (StringUtils.isBlank(regCode)) {
            return;
        }
        this.regCode = regCode;
    }

    public void setTaxNo(String taxNo) {
        if (StringUtils.isBlank(taxNo)) {
            return;
        }
        this.taxNo = taxNo;
    }

    public void setOrgNo(String orgNo) {
        if (StringUtils.isBlank(orgNo)) {
            return;
        }
        this.orgNo = orgNo;
    }

    public void setRegNo(String regNo) {
        if (StringUtils.isBlank(regNo)) {
            return;
        }
        this.regNo = regNo;
    }

    public void setRegAddr(String regAddr) {
        if (StringUtils.isBlank(regAddr)) {
            return;
        }
        this.regAddr = regAddr;
    }

    public void setDistrict(String district) {
        if (StringUtils.isBlank(district)) {
            return;
        }
        this.district = district;
    }

    public void setScope(String scope) {
        if (StringUtils.isBlank(scope)) {
            return;
        }
        this.scope = scope;
    }

    public void setStartDate(String startDate) {
        if (StringUtils.isBlank(startDate)) {
            return;
        }
        this.startDate = startDate;
    }

    public void setOpenTime(String openTime) {
        if (StringUtils.isBlank(openTime)) {
            return;
        }
        this.openTime = openTime;
    }

    public void setAnnualDate(String annualDate) {
        if (StringUtils.isBlank(annualDate)) {
            return;
        }
        this.annualDate = annualDate;
    }

    public void setRegCapital(String regCapital) {
        if (StringUtils.isBlank(regCapital)) {
            return;
        }
        this.regCapital = regCapital;
    }

    public void setRealCapital(String realCapital) {
        if (StringUtils.isBlank(realCapital)) {
            return;
        }
        this.realCapital = realCapital;
    }

    public void setIndustry(String industry) {
        if (StringUtils.isBlank(industry)) {
            return;
        }
        this.industry = industry;
    }

    public void setEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return;
        }
        this.email = email;
    }

    public void setBankInfo(String bankInfo) {
        if (StringUtils.isBlank(bankInfo)) {
            return;
        }
        this.bankInfo = bankInfo;
    }

    public boolean basicValid() {
        if (StringUtils.isBlank(entName) || StringUtils.isBlank(compId) || StringUtils.isBlank(regAddr)) {
            return false;
        }
        regNo = StringUtils.isBlank(regNo) ? regCode : regNo;
        return !StringUtils.isBlank(unifiedCode) || !StringUtils.isBlank(taxNo) || !StringUtils.isBlank(regNo);
    }

    public String errId() {
        if (entName == null) {
            entName = "";
        }
        if (compId == null) {
            compId = "";
        }
        return entName + ID_DELIMITER + compId;
    }

    public void merge(Company company) {
        if (company == null) {
            return;
        }
        ReflectionUtils.doWithFields(Company.class, field -> {
            ReflectionUtils.makeAccessible(field);
            if (field.getModifiers() == Modifier.STATIC || field.getModifiers() == Modifier.FINAL) {
                return;
            }
            Object object = ReflectionUtils.getField(field, company);
            if (object != null) {
                ReflectionUtils.setField(field, this, object);
            }
        });
    }

    public static Company asParam() {
        return new Company();
    }

    public Company id(String id) {
        setCompId(id);
        return this;
    }

    public Company name(String name) {
        setEntName(name);
        return this;
    }

    @Override
    public boolean O_K() {
        String regCode = StringUtils.isBlank(getRegCode()) ? getLicenseNumber() : getRegCode();
        String regNo;
        if (StringUtils.isNotBlank(regCode)) {
            regNo = regCode;
        } else {
            regNo = StringUtils.isBlank(getRegNo()) ? regCode : getRegNo();
        }
        setRegNo(regNo);
        setRegCode(regNo);
        return basicValid();
    }
}
