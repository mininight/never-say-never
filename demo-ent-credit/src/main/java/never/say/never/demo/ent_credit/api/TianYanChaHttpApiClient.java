/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.ImmutableMap;
import feign.Body;
import feign.Param;
import feign.RequestLine;
import feign.RequestTemplate;
import never.say.never.demo.ent_credit.api.dto.TianYanChaCompany;
import never.say.never.demo.ent_credit.entity.Company;
import never.say.never.demo.ent_credit.http.HeaderTemplate;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.http.RefreshingCookie;
import never.say.never.demo.ent_credit.util.JacksonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Objects;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-01
 */
public interface TianYanChaHttpApiClient extends HttpEntCreditApi {
    String BASE_API_HOST = "capi.tianyancha.com";
    String BASE_API_URL = "https://" + BASE_API_HOST;
    String BASE_HTML_HOST = "www.tianyancha.com";
    String BASE_HTML_URL = "https://" + BASE_HTML_HOST;

    @Override
    default void prepareHeaders(HttpApiRequestContext context) {
        RequestTemplate request = context.getRequest();
        String path = request.url();
        String method = request.method();
        if (isHtmlRequest(request)) {
            context.getCustomHeaders().clear();
            context.getCustomHeaders().putAll(HeaderTemplate.TianYanCha_Html_header.getKvMap());
            context.updateHeader("cookie", RefreshingCookie.TianYanCha_Html_cookie.val());
            context.updateHeader("Host", BASE_HTML_HOST);
            request.target(BASE_HTML_URL);
            return;
        } else {
            context.getCustomHeaders().clear();
            context.getCustomHeaders().putAll(HeaderTemplate.TianYanCha_header.getKvMap());
            context.updateHeader("Host", BASE_API_HOST);
            context.updateHeader("Origin", BASE_HTML_URL);
            request.target(BASE_API_URL);
        }
        if ("POST".equalsIgnoreCase(method)) {
            OPTIONS(path);
        }
        if ("OPTIONS".equalsIgnoreCase(method)) {
            context.updateHeader("Accept", "*/*");
            context.updateHeader("Access-Control-Request-Method", "POST");
            context.updateHeader("Access-Control-Request-Headers",
                    "content-type,version,x-auth-token,x-tycid");
            return;
        }
        context.updateHeader("X-AUTH-TOKEN", RefreshingCookie.TianYanCha_cookie.val());
    }

    /**
     * 跨域
     *
     * @return
     */
    @RequestLine("OPTIONS {path}")
    void OPTIONS(@Param("path") String path);

    /**
     * 模糊匹配企业
     *
     * @return
     */
    @RequestLine("POST /cloud-tempest/search/suggest/v5?_={tmsp}")
    @feign.Headers("Content-Type: application/json")
    @Body("{body}")
    JSONObject suggest(@Param("body") String body, @Param("tmsp") long tmsp);


    /**
     * 公司信息页面
     *
     * @return
     */
    @RequestLine("GET /company/{id}")
    @feign.Headers("Content-Type: text/html")
    String getCompanyDetailHtml(@Param("id") String id);


    @Override
    default JSONObject checkJsonResult(ExecSupplier<JSONObject> supplier) throws Throwable {
        JSONObject apiResult = supplier.get();
        apiResult = apiResult == null ? new JSONObject() : apiResult;
        if (!"ok".equalsIgnoreCase(apiResult.getString("state"))) {
            throw new IllegalAccessException(String.format("接口无法访问，apiResult:%s", apiResult.toJSONString()));
        }
        return apiResult;
    }

    @Override
    default Company grabEntCreditBasic(Company compParam) throws Throwable {
        if (compParam == null || StringUtils.isBlank(compParam.getEntName())) {
            return compParam;
        }
        String companyName = compParam.getEntName();
        JSONObject apiResult = checkJsonResult(() -> suggest(JSON.toJSONString(ImmutableMap.of(
                "keyword", companyName
                )), System.currentTimeMillis()
        ));
        List<JSONObject> dataList = apiResult.getJSONArray("data").toJavaList(JSONObject.class);
        if (CollectionUtils.isEmpty(dataList)) {
            return compParam;
        }
        String id = null;
        for (JSONObject data : dataList) {
            if (Objects.equals(companyName, data.getString("comName"))) {
                id = data.getString("id");
                break;
            }
        }
        if (StringUtils.isBlank(id)) {
            return compParam;
        }
        String pageDoc = getCompanyDetailHtml(id);
        Document document = Jsoup.parse(pageDoc);
        Element element = document.selectFirst("script#__NEXT_DATA__");
        JSONObject jsonData = JacksonUtil.JSON_MAPPER.readValue(element.html(), JSONObject.class);
        apiResult = jsonData.getJSONObject("props").getJSONObject("pageProps")
                .getJSONObject("dehydratedState").getJSONArray("queries").toJavaList(JSONObject.class)
                .stream().filter(jsonObject -> jsonObject.getString("queryHash").contains("/companyinfo")).findFirst()
                .orElse(null);
        if (apiResult == null) {
            return compParam;
        }
        JSONObject companyJson = apiResult.getJSONObject("state").getJSONObject("data").getJSONObject("data");
        applyCompanyJson(compParam, companyJson);
        return compParam;
    }

    @Override
    default void applyCompanyJson(Company compParam, JSONObject companyJson) {
        if (companyJson == null || companyJson.isEmpty()) {
            return;
        }
        TianYanChaCompany tianYanChaCompany = companyJson.toJavaObject(TianYanChaCompany.class);
        compParam.setEntLogoWord(tianYanChaCompany.getAlias());
        compParam.setUnifiedCode(tianYanChaCompany.getCompanyCreditCode());
        compParam.setRegNo(tianYanChaCompany.getRegNumber());
        compParam.setRegCode(compParam.getRegNo());
        compParam.setTaxNo(tianYanChaCompany.getTaxNumber());
        compParam.setOrgNo(tianYanChaCompany.getOrgNumber());
        compParam.setRegAddr(tianYanChaCompany.getRegLocation());
        compParam.setOpenStatus(tianYanChaCompany.getRegStatus());
        compParam.setScope(tianYanChaCompany.getBusinessScope());
        compParam.setIndustry(tianYanChaCompany.industryInfo());
        compParam.setRegCapital(tianYanChaCompany.getRegCapital());
        compParam.setStartDate(tianYanChaCompany.registrationDate());
        compParam.setOpenTime(tianYanChaCompany.getExpiryDate());
        compParam.setAnnualDate(tianYanChaCompany.approvedTime());
        compParam.setEmail(tianYanChaCompany.email());
        compParam.setTelephone(tianYanChaCompany.getPhoneNumber());
        compParam.setAuthority(tianYanChaCompany.getRegInstitute());
        compParam.setLegalCompNum(tianYanChaCompany.legalCompanyNum());
    }
}
