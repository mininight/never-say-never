/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Preconditions;
import feign.Param;
import feign.RequestLine;
import feign.RequestTemplate;
import never.say.never.demo.ent_credit.entity.Company;
import never.say.never.demo.ent_credit.http.HeaderTemplate;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.http.RefreshingCookie;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlunit.HttpMethod;
import org.htmlunit.WebRequest;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-07
 */
public interface QiChaChaHttpApiClient extends QiChaChaHttpApi {

    String BASE_HOST = "www.qcc.com";
    String BASE_URL = "https://" + BASE_HOST;

    @Override
    default void prepareHeaders(HttpApiRequestContext context) {
        RequestTemplate request = context.getRequest();
        String path = request.url();
        context.updateHeader(":method", request.method());
        context.updateHeader(":authority", BASE_HOST);
        context.updateHeader(":scheme", "https");
        context.updateHeader(":path", path);
        context.updateHeader("cookie", RefreshingCookie.QiChaCha_cookie.val());
        context.getCustomHeaders().putAll(HeaderTemplate.QiChaCha_header.getKvMap());
    }

    /**
     * 公司信息页面
     *
     * @return
     */
    @RequestLine("GET /firm/{id}.html")
    @feign.Headers({"Content-Type: text/html", "referer: " + BASE_URL + "/"})
    String getCompanyDetailHtml(@Param("id") String id);

    @Override
    default Company grabEntCreditBasic(Company compParam) throws Throwable {
        if (compParam == null || StringUtils.isBlank(compParam.getEntName())) {
            return compParam;
        }
        String companyName = compParam.getEntName();
        String key = URLEncoder.encode(companyName, UTF_8);
        URL compPageURL = new URL(QiChaChaHttpApiClient.BASE_URL + "/web/search?key=" + key);
        WebRequest webRequest = new WebRequest(compPageURL, HttpMethod.GET);
        HeaderTemplate.QiChaCha_header.getKvMap().forEach(webRequest::setAdditionalHeader);
        webRequest.setAdditionalHeader("cookie", RefreshingCookie.QiChaCha_cookie.val());
        HtmlPage compPage = HttpApiClient.CHROME_BROWSER.getPage(webRequest);
        HtmlElement body = compPage.getBody();
        List<HtmlElement> tableList = body.getElementsByAttribute("table", "class", "app-ltable ntable ntable-list ntable ntable-list");
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(tableList),
                "未查询到公司：" + companyName);
        Document table = Jsoup.parse(tableList.get(0).asXml());
        Elements elements = table.select("tr");
        String id = null;
        for (Element element : elements) {
            Element compLink = element.selectFirst("span.copy-title > a");
            String compName = compLink.selectFirst("span").text();
            compName = compName.replaceAll("<em>", "");
            compName = compName.replaceAll("</em>", "");
            if (Objects.equals(companyName, compName)) {
                String href = compLink.attribute("href").getValue();
                id = href.substring(href.lastIndexOf("/") + 1, href.lastIndexOf("."));
                break;
            }
        }
        if (StringUtils.isBlank(id)) {
            return compParam;
        }
        String pageDoc = getCompanyDetailHtml(id);
        Document document = Jsoup.parse(pageDoc);
        Element element = document.selectFirst("body > script+script");
        String jsonLine = element.html();
        jsonLine = jsonLine.substring("window.__INITIAL_STATE__=".length());
        if (jsonLine.contains(";(function()")) {
            jsonLine = jsonLine.substring(0, jsonLine.indexOf(";(function()"));
        }
        JSONObject companyJson = JSON.parseObject(jsonLine).getJSONObject("company")
                .getJSONObject("companyDetail");
        applyCompanyJson(compParam, companyJson);
        return compParam;
    }
}
