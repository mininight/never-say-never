/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.http;

import lombok.Getter;
import lombok.Setter;
import never.say.never.demo.ent_credit.api.HttpApiClient;
import never.say.never.demo.ent_credit.configure.EntCreditBeanUnit;
import never.say.never.demo.ent_credit.entity.Company;
import never.say.never.demo.ent_credit.entity.Person;
import org.apache.commons.lang3.StringUtils;

import static never.say.never.demo.ent_credit.http.HttpApiClientPanel.*;
import static never.say.never.demo.ent_credit.http.RefreshingCookie.QiChaCha_cookie;
import static never.say.never.demo.ent_credit.http.RefreshingCookie.QiChaCha_token;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-13
 */
public class HttpApiSLBPoints {

    @Getter
    @Setter
    private EntCreditBeanUnit beanUnit;

    private final HttpApiSLB<Company> companyInfoBackup = HttpApiSLB.of(
            (execId, company) -> {
                try {
                    if (!HttpApiClient.frequencyOk(QiChaCha)
                            || StringUtils.isBlank(QiChaCha_cookie.val())) {
                        return company;
                    }
                    return QiChaCha.grabEntCreditBasic(company);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "QiChaCha.grabCompany", e);
                    return company;
                }
            },
            (execId, company) -> {
                try {
                    if (!HttpApiClient.frequencyOk(QiChaCha)
                            || QiChaCha_token.isExpired()
                            || StringUtils.isBlank(QiChaCha_token.val())) {
                        return company;
                    }
                    return QiChaCha_WX.grabEntCreditBasic(company);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "QiChaCha_WX.grabCompany", e);
                    return company;
                }
            },
            (execId, company) -> {
                try {
                    if (!HttpApiClient.frequencyOk(TianYanCha, 1)) {
                        return company;
                    }
                    return TianYanCha.grabEntCreditBasic(company);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "TianYanCha.grabCompany", e);
                    return company;
                }
            },
            (execId, company) -> {
                try {
                    return YiQiCha.grabEntCreditBasic(company);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "YiQiCha.grabCompany", e);
                    return company;
                }
            },
            (execId, company) -> {
                try {
                    return QiChaMao.grabEntCreditBasic(company);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "YiQiCha.grabCompany", e);
                    return company;
                }
            }
    );

    @Getter
    private final HttpApiSLB<Company> companyInfo = HttpApiSLB.of(
            (execId, company) -> {
                try {
                    return AiQiCha.grabEntCreditBasic(company);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "AiQiCha.grabCompanyDetail:byRisk", e);
                    return company;
                }
            },
            (execId, company) -> {
                try {
                    return AiQiCha.grabCompanyDetail(company, false);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "AiQiCha.grabCompanyDetail:byRisk", e);
                    return company;
                }
            },
            (execId, company) -> {
                try {
                    return AiQiCha.grabCompanyDetail(company, true);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "AiQiCha.grabCompanyDetail", e);
                    return company;
                }
            },
            (execId, company) -> {
                try {
                    company = AiQiCha.grabCompanySimpleInfo(company, true);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "AiQiCha.grabCompanySimpleInfo:advance", e);
                    return company;
                }
                try {
                    return companyInfoBackup.apply(execId, company);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "companyBackupApiSLB", e);
                    return company;
                }
            },
            (execId, company) -> {
                try {
                    company = AiQiCha.grabCompanySimpleInfo(company, false);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "AiQiCha.grabCompanySimpleInfo:icpsearch", e);
                    return company;
                }
                try {
                    return companyInfoBackup.apply(execId, company);
                } catch (Throwable e) {
                    beanUnit.getRecorder().logCompanyErr(company, "companyBackupApiSLB", e);
                    return company;
                }
            }
    );

    @Getter
    private final HttpApiSLB<Person> personInfo = HttpApiSLB.of(
            (execId, person) -> {
                try {
                    return AiQiCha.grabPersonDetail(person, false);
                } catch (Exception e) {
                    beanUnit.getRecorder().logPersonErr(person, "AiQiCha.grabPersonDetail:byRisk", e);
                    return person;
                }
            },
            (execId, person) -> {
                try {
                    return AiQiCha.grabPersonDetail(person, true);
                } catch (Exception e) {
                    beanUnit.getRecorder().logPersonErr(person, "AiQiCha.grabPersonDetail", e);
                    return person;
                }
            },
            (execId, person) -> {
                try {
                    return AiQiCha.grabPersonSimpleInfo(person);
                } catch (Exception e) {
                    beanUnit.getRecorder().logPersonErr(person, "AiQiCha.grabPersonSimpleInfo", e);
                    return person;
                }
            }
    );
}
