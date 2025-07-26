/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.http;

import never.say.never.demo.ent_credit.api.*;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-13
 */
public class HttpApiClientPanel {

    public static final HttpApiClientRequestInterceptor httpApiInterceptor = new HttpApiClientRequestInterceptor();
    // 爱企查
    public static final AiQiChaHttpApiClient AiQiCha = HttpApiClient.newClient(AiQiChaHttpApiClient.class,
            AiQiChaHttpApiClient.BASE_URL, httpApiInterceptor);
    // 企查查PC
    public static final QiChaChaHttpApiClient QiChaCha = HttpApiClient.newClient(QiChaChaHttpApiClient.class,
            QiChaChaHttpApiClient.BASE_URL, httpApiInterceptor);
    // 企查查wx
    public static final QiChaChaWxHttpApiClient QiChaCha_WX = HttpApiClient.newClient(QiChaChaWxHttpApiClient.class,
            QiChaChaWxHttpApiClient.BASE_URL, httpApiInterceptor);
    // 天眼查
    public static final TianYanChaHttpApiClient TianYanCha = HttpApiClient.newClient(TianYanChaHttpApiClient.class,
            TianYanChaHttpApiClient.BASE_API_URL, httpApiInterceptor);
    // 启信宝
    public static final QiXinBaoHttpApiClient QiXinBao = HttpApiClient.newClient(QiXinBaoHttpApiClient.class,
            QiXinBaoHttpApiClient.BASE_URL, httpApiInterceptor);
    // 启信宝wx
    public static final QiXinBaoWxHttpApiClient QiXinBao_WX = HttpApiClient.newClient(QiXinBaoWxHttpApiClient.class,
            QiXinBaoWxHttpApiClient.BASE_URL, httpApiInterceptor);
    // 亿企查
    public static final YiQiChaHttpApiClient YiQiCha = HttpApiClient.newClient(YiQiChaHttpApiClient.class,
            YiQiChaHttpApiClient.BASE_URL, httpApiInterceptor);
    // 企查猫
    public static final QiChaMaoHttpClientApi QiChaMao = HttpApiClient.newClient(QiChaMaoHttpClientApi.class,
            QiChaMaoHttpClientApi.BASE_URL, httpApiInterceptor);

}
