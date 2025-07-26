/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import com.alibaba.fastjson2.JSONObject;
import feign.Body;
import feign.Param;
import feign.RequestLine;
import feign.RequestTemplate;
import never.say.never.demo.ent_credit.entity.Company;
import never.say.never.demo.ent_credit.http.HeaderTemplate;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-11
 */
@Deprecated
public interface QiXinBaoWxHttpApiClient extends HttpEntCreditApi {
    String BASE_HOST = "backend-mini-program-service-ms.qixin.com";
    String BASE_URL = "https://" + BASE_HOST;

    @Override
    default void prepareHeaders(HttpApiRequestContext context) {
        RequestTemplate request = context.getRequest();
        String method = request.method();
        String path = request.url();
        context.getCustomHeaders().putAll(HeaderTemplate.QiXinBao_wxheader.getKvMap());
        context.updateHeader(":method", method);
        context.updateHeader(":authority", BASE_HOST);
        context.updateHeader(":scheme", "https");
        context.updateHeader(":path", path);
//        context.updateHeader("cookie", RefreshingCookie.QiXinBao_wxcookie.val());
        System.out.println();
    }

    /**
     * 模糊匹配企业
     *
     * @return
     */
    @RequestLine("POST /search/advanceSearch")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject suggest(@Param("body") String body);

    /**
     * 企业信息
     *
     * @return
     */
    @RequestLine("POST /enterprise/getEnterpriseInformation")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject getEnterpriseInformation(@Param("body") String body);


    @Override
    default Company grabEntCreditBasic(Company compParam) throws Throwable {
        if (compParam == null || StringUtils.isBlank(compParam.getEntName())) {
            return compParam;
        }
        String companyName = compParam.getEntName();
        JSONObject query = new JSONObject();
        query.put("keyword", companyName);
        query.put("start", 0);
        query.put("hit", 10);
        query.put("sortBy", "");
        query.put("province", "");
        query.put("cityCode", "");
        query.put("district", "");
        query.put("induCodeL1", "");
        query.put("induCodeL2", "");
        JSONObject jsonObject = suggest(query.toJSONString());
        return compParam;
    }

    @Override
    default void applyCompanyJson(Company compParam, JSONObject companyJson) {

    }
}
