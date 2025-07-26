/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.http;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.Getter;
import lombok.Setter;
import never.say.never.demo.ent_credit.api.HttpApiClient;

import java.util.Map;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-30
 */
@Getter
@Setter
public class HttpApiClientRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate request) {
        HttpApiRequestContext context = HttpApiRequestContext.getCurrent();
        context.setRequest(request);
        HttpApiClient httpApiClient = context.getHttpApiClient();
        if (httpApiClient != null) {
            httpApiClient.prepareHeaders(context);
        }
        //
        for (Map.Entry<String, String> entry : context.getCustomHeaders().entrySet()) {
            request.header(entry.getKey(), entry.getValue());
        }
    }
}
