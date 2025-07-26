/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import com.alibaba.fastjson2.JSONObject;
import feign.Param;
import feign.RequestLine;
import feign.RequestTemplate;
import never.say.never.demo.ent_credit.entity.Company;
import never.say.never.demo.ent_credit.http.HeaderTemplate;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.http.RefreshingCookie;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-06
 */
public interface QiChaChaWxHttpApiClient extends QiChaChaHttpApi {
    String BASE_HOST = "xcx.qcc.com";
    String BASE_URL = "https://" + BASE_HOST;

    @Override
    default void prepareHeaders(HttpApiRequestContext context) {
        RequestTemplate request = context.getRequest();
        String timestamp = System.currentTimeMillis() + "";
        String token = RefreshingCookie.QiChaCha_token.val();
        request.query("token", token);
        request.query("t", timestamp);
        context.updateHeader(":method", request.method());
        context.updateHeader(":authority", BASE_HOST);
        context.updateHeader(":scheme", "https");
        context.updateHeader(":path", request.url());
        context.updateHeader("authmini", "Bearer " + token);
        context.getCustomHeaders().putAll(HeaderTemplate.QiChaCha_wxheader.getKvMap());
    }

    /**
     * 公司信息
     *
     * @return
     */
    @RequestLine("GET /mp-weixin/forwardApp/v3/base/advancedSearch?pageIndex=1&needGroup=yes&insuredCntStart=&insuredCntEnd=&recCapitalBegin=&recCapitalEnd=&registCapiBegin=&registCapiEnd=&countyCode=&province=&sortField=&isSortAsc=&searchKey={searchKey}")
    @feign.Headers({
            "Content-Type: application/json",
            "qcc-currentpage: /pages/search/index/index",
            "qcc-refpage: /pages/home/index"
    })
    JSONObject suggest(@Param("searchKey") String searchKey);


    /**
     * 公司信息
     *
     * @return
     */
    @RequestLine("GET /mp-weixin/forwardApp/v6/base/getEntDetail?unique={id}")
    @feign.Headers({
            "Content-Type: application/json",
            "qcc-currentpage: /company-subpackages/business/index",
            "qcc-refpage: /company-subpackages/detail/index"
    })
    JSONObject getCompanyDetail(@Param("id") String id);


    @Override
    default Company grabEntCreditBasic(Company compParam) throws Throwable {
        if (compParam == null || StringUtils.isBlank(compParam.getEntName())) {
            return compParam;
        }
        String companyName = compParam.getEntName();
        JSONObject apiResult = checkJsonResult(() -> suggest(companyName));
        String id = apiResult.getJSONObject("result").getJSONArray("Result")
                .toJavaList(JSONObject.class)
                .stream().filter(jsonObject -> {
                    String name = jsonObject.getString("Name");
                    name = name.replaceAll("<em>", "");
                    name = name.replaceAll("</em>", "");
                    return Objects.equals(name, companyName);
                }).findFirst().orElseGet(JSONObject::new).getString("KeyNo");
        if (StringUtils.isBlank(id)) {
            return compParam;
        }
        apiResult = checkJsonResult(() -> getCompanyDetail(id));
        JSONObject compJson = apiResult.getJSONObject("result").getJSONObject("Company");
        applyCompanyJson(compParam, compJson);
        return compParam;
    }
}
