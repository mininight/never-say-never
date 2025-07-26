/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.logger;

import com.alibaba.fastjson2.JSON;
import feign.RequestTemplate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import never.say.never.demo.ent_credit.entity.Company;
import never.say.never.demo.ent_credit.entity.ExecFailed;
import never.say.never.demo.ent_credit.entity.Person;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-07
 */
@Slf4j
@Getter
public class ExecFailedRecorder {

    private final ExecFailedLoggerRepository repository;

    public ExecFailedRecorder(ExecFailedLoggerRepository repository) {
        this.repository = repository;
    }

    public void logCompanyErr(Company company, String method, String errMsg, Object... args) {
        logCompanyErr(company.errId(), method, errMsg, null, args);
    }

    public void logCompanyErr(Company company, String method, Throwable t) {
        logCompanyErr(company.errId(), method, null, t);
    }

    public void logCompanyErr(Company company, String method, String errMsg, Throwable t, Object... args) {
        logCompanyErr(company.errId(), method, errMsg, t, args);
    }

    public void logCompanyErr(String id, String method, String errMsg, Object... args) {
        logCompanyErr(id, method, errMsg, null, args);
    }

    public void logCompanyErr(String id, String method, Throwable t) {
        logCompanyErr(id, method, null, t);
    }

    public void logCompanyErr(String id, String method, String errMsg, Throwable t, Object... args) {
        logErr(id, "company", method, errMsg, t, args);
    }

    public void logPersonErr(Person person, String method, String errMsg, Object... args) {
        logPersonErr(person, method, errMsg, null, args);
    }

    public void logPersonErr(Person person, String method, Throwable t) {
        logPersonErr(person.errId(), method, null, t);
    }

    public void logPersonErr(Person person, String method, String errMsg, Throwable t, Object... args) {
        logPersonErr(person.errId(), method, errMsg, t, args);
    }

    public void logPersonErr(String id, String method, String errMsg, Object... args) {
        logPersonErr(id, method, errMsg, null, args);
    }

    public void logPersonErr(String id, String method, Throwable t) {
        logPersonErr(id, method, null, t);
    }

    public void logPersonErr(String id, String method, String errMsg, Throwable t, Object... args) {
        logErr(id, "person", method, errMsg, t, args);
    }

    public void logErr(String id, String type, String method, Throwable t) {
        logErr(id, type, method, null, t);
    }

    public void logErr(String id, String type, String method, String errMsg, Object... args) {
        logErr(id, type, method, errMsg, null, args);
    }

    public void logErr(String id, String type, String method, String errMsg, Throwable t, Object... args) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        try {
            String errMsgTxt = errMsg == null ? "" : errMsg;
            if (StringUtils.isNotBlank(errMsgTxt)) {
                errMsgTxt = args == null || args.length < 1 ? errMsgTxt : String.format(errMsgTxt, args);
            }
            if (t != null) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream(); PrintStream printStream = new PrintStream(out)) {
                    t.printStackTrace(printStream);
                    if (StringUtils.isNotBlank(errMsgTxt)) {
                        errMsgTxt = errMsgTxt + " => " + out.toString(StandardCharsets.UTF_8);
                    } else {
                        errMsgTxt = out.toString(StandardCharsets.UTF_8);
                    }
                }
            }
            ExecFailed execFailed = new ExecFailed();
            execFailed.setId(id);
            execFailed.setType(type);
            execFailed.setMethod(method);
            execFailed.setErr_msg(errMsgTxt);
            execFailed.setAssociation_reqeust(getHttpRequestInfo());
            execFailed.setLevel(HttpApiRequestContext.getCurrent().getLevel());
            log.warn(execFailed.toString());
            if (repository == null) {
                return;
            }
            repository.logExecFailed(execFailed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getHttpRequestInfo() {
        RequestTemplate request = HttpApiRequestContext.getCurrent().getRequest();
        if (request == null) {
            return "RequestTemplate missed";
        }
        String info = " " + request.method() + " " + request.url();
        if (request.body() != null && request.body().length > 0) {
            info = info + "\n" + " Payload: " + JSON.toJSONString(request.body());
        }
        return info;
    }
}
