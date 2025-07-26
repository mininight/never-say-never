/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-09
 */
@Data
public class QiChaChaCompany2 {
    private String KeyNo;
    private String Name;
    private String ShortName;
    private String Email;
    private String contactNo;
    private DJInfo DJInfo;

    @Data
    public static class DJInfo {
        private String address;
        private String certificatePeriod;
        private String registCapi;
        private String startDate;
        private String fazhengAuthority;
        private String status;
        private String creditCode;
        private String scope;
        private JSONObject Oper;
        private String econKind;

        public String getStartDate() {
            if (startDate == null) {
                return null;
            }
            if (StringUtils.isNumeric(startDate)) {
                startDate = DateFormatUtils.format(Long.parseLong(startDate) * 1000, "yyyy-MM-dd");
            }
            return startDate;
        }
    }
}
