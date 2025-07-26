/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.http;

import feign.RequestTemplate;
import lombok.Getter;
import lombok.Setter;
import never.say.never.demo.ent_credit.api.HttpApiClient;
import never.say.never.demo.ent_credit.configure.EntCreditBeanUnit;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-30
 */
@Getter
@Setter
public class HttpApiRequestContext {

    private static final ThreadLocal<HttpApiRequestContext> INSTANCE = new ThreadLocal<>();

    private HttpApiClient httpApiClient;
    private RequestTemplate request;
    private final Map<String, String> customHeaders = new LinkedHashMap<>();
    private Integer level;
    private EntCreditBeanUnit beanUnit;

    {
        Runtime.getRuntime().removeShutdownHook(new Thread("HttpApiRequestContext_CLEAN") {
            @Override
            public void run() {
                HttpApiRequestContext.clean();
            }
        });
    }

    public void updateHeader(String headerName, String headerValue) {
        customHeaders.put(headerName, headerValue);
    }

    public static HttpApiRequestContext getCurrent() {
        if (INSTANCE.get() == null) {
            synchronized (HttpApiRequestContext.class) {
                if (INSTANCE.get() == null) {
                    INSTANCE.set(new HttpApiRequestContext());
                }
            }
        }
        return INSTANCE.get();
    }

    public static void clean() {
        INSTANCE.remove();
    }
}
