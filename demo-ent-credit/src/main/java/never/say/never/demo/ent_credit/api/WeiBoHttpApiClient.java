/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import com.alibaba.fastjson2.JSONObject;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import feign.RequestTemplate;
import never.say.never.demo.ent_credit.http.HeaderTemplate;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.http.RefreshingCookie;

import java.util.Map;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-31
 */
public interface WeiBoHttpApiClient extends HttpApiClient {

    String BASE_HOST = "weibo.com";
    String BASE_URL = "https://" + BASE_HOST;

    @Override
    default void prepareHeaders(HttpApiRequestContext context) {
        RequestTemplate request = context.getRequest();
        String path = request.url();
        context.updateHeader(":method", request.method());
        context.updateHeader(":authority", BASE_HOST);
        context.updateHeader(":scheme", "https");
        context.updateHeader(":path", path);
        context.updateHeader("cookie", RefreshingCookie.WeiBo_cookie.val());
        context.getCustomHeaders().putAll(HeaderTemplate.WeiBo_header.getKvMap());
    }
    //

    /**
     * 热搜榜 type: mine , hot
     *
     * @param type
     * @return
     */
    @RequestLine("GET /ajax/side/searchBand?type={type}&last_tab={lastTab}&last_tab_time={lastTabTime}")
    @feign.Headers("Content-Type: application/json")
    JSONObject searchBand(@Param("type") String type, @Param("lastTab") String lastTab, @Param("lastTabTime") int lastTabTime);


    /**
     * 分享/发布 type: mine , hot
     *
     * @param queryMap content ; action_code:11 ; _t:0 ; rnd
     * @return
     */
    @RequestLine("POST /Ajax_Mblog/publish?__rnd={rnd}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject publish(@QueryMap Map<String, Object> queryMap);



    /**
     * 转发 https://s.weibo.com/Ajax_Mblog/repost?__rnd=1725120646502
     *
     * @param queryMap mid ; reason ; action_code: 11,31 ; is_comment_base: 1 ; location: ; _t: 0
     * @return
     */
    @RequestLine("POST Ajax_Mblog/repost?__rnd={rnd}")
    @feign.Headers("Content-Type: application/x-www-form-urlencoded")
    JSONObject repost(@QueryMap Map<String, Object> queryMap);
}
