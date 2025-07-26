/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import feign.Param;
import feign.RequestLine;
import feign.RequestTemplate;
import never.say.never.demo.ent_credit.entity.Company;
import never.say.never.demo.ent_credit.http.HeaderTemplate;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.http.RefreshingCookie;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-10
 */
@Deprecated
public interface QiXinBaoHttpApiClient extends HttpEntCreditApi {

    String BASE_HOST = "www.qixin.com";
    String BASE_URL = "https://" + BASE_HOST;

    @Override
    default void prepareHeaders(HttpApiRequestContext context) {
        RequestTemplate request = context.getRequest();
        String method = request.method();
        String path = request.url();
        context.updateHeader(":method", method);
        context.updateHeader(":authority", BASE_HOST);
        context.updateHeader(":scheme", "https");
        context.updateHeader(":path", path);
        if (isHtmlRequest(request)) {
            context.getCustomHeaders().putAll(HeaderTemplate.QiXinBao_Html_header.getKvMap());
            context.updateHeader("cookie", RefreshingCookie.QiXinBao_cookie.val());
            context.updateHeader("referer", BASE_URL + path);
        }
    }

    /**
     * 模糊匹配企业
     *
     * @return
     */
    @RequestLine("GET /search?key={key}")
    @feign.Headers("Content-Type: text/html;charset=utf-8")
    String suggest(@Param("key") String key);

    /**
     * 公司信息页面
     *
     * @return
     */
    @RequestLine("GET /company/{id}")
    @feign.Headers("Content-Type: text/html")
    String getCompanyDetailHtml(@Param("id") String id);


    @Override
    default Company grabEntCreditBasic(Company compParam) throws Throwable {
        if (compParam == null || StringUtils.isBlank(compParam.getEntName())) {
            return compParam;
        }
        String companyName = compParam.getEntName();
        String pageHtml = suggest(companyName);
        Document pageDoc = Jsoup.parse(pageHtml);
        String json = pageDoc.selectFirst("#__NUXT_DATA__").html();
        JSONArray searchJsonArray = JSON.parseArray(json);
        List<Integer> itemIndexList = searchJsonArray.getJSONArray(searchJsonArray.getJSONObject(searchJsonArray.getJSONObject(searchJsonArray
                .getJSONObject(searchJsonArray.getJSONObject(1).getInteger("data")).getInteger("search-index"))
                .getInteger("companyInfo")).getInteger("items")).toJavaList(Integer.class);
        String id = null;
        for (Integer itemIndex : itemIndexList) {
            JSONObject itemPropIndexMap = searchJsonArray.getJSONObject(itemIndex);
            int matchNameIndex = searchJsonArray.getJSONArray(itemPropIndexMap.getInteger("match_items")).getInteger(0);
            String compName = searchJsonArray.getString(searchJsonArray.getJSONObject(matchNameIndex).getInteger("match_value"));
            compName = compName.replaceAll("<em>", "");
            compName = compName.replaceAll("</em>", "");
            if (companyName.equals(compName)) {
                id = searchJsonArray.getString(itemPropIndexMap.getInteger("eid"));
                break;
            }
        }
        if (StringUtils.isBlank(id)) {
            return compParam;
        }
        pageHtml = getCompanyDetailHtml(id);
        pageDoc = Jsoup.parse(pageHtml);
        json = pageDoc.selectFirst("#__NUXT_DATA__").html();
        JSONArray compJsonArray = JSONArray.parseArray(json);
        JSONObject companyJson = compJsonArray.getJSONObject(compJsonArray.getJSONObject(
                compJsonArray.getJSONObject(1).getIntValue("pinia")
        ).getIntValue("company"));
        int size = compJsonArray.size();
        companyJson.entrySet().forEach(entry -> {
            if (entry.getValue() == null || (Integer) entry.getValue() >= size) {
                entry.setValue(null);
                return;
            }
            if ("historyNames".equals(entry.getKey())) {
                entry.setValue(compJsonArray.getJSONArray((Integer) entry.getValue()).toJavaList(Integer.class)
                        .stream().map(compJsonArray::getString).collect(Collectors.joining("; ")));
                return;
            }
            entry.setValue(compJsonArray.getString((Integer) entry.getValue()));
        });
        return compParam;
    }

    @Override
    default void applyCompanyJson(Company compParam, JSONObject companyJson) {

    }
}
