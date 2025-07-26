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
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.alibaba.fastjson2.util.TypeUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoClient;
import feign.*;
import feign.okhttp.OkHttpClient;
import lombok.Data;
import never.say.never.demo.ent_credit.api.annotation.DWH_Collect;
import never.say.never.demo.ent_credit.api.annotation.DWH_DB;
import never.say.never.demo.ent_credit.configure.EntCreditBeanUnit;
import never.say.never.demo.ent_credit.http.HttpApiClientRequestInterceptor;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.http.HttpCompressDecoder;
import never.say.never.demo.ent_credit.util.FuConverter;
import never.say.never.demo.ent_credit.util.FuSupplier;
import never.say.never.demo.ent_credit.util.HttpApiPageDataGripper;
import never.say.never.demo.ent_credit.util.JacksonUtil;
import okhttp3.ConnectionSpec;
import okhttp3.Protocol;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlunit.BrowserVersion;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.WebClient;
import org.htmlunit.javascript.SilentJavaScriptErrorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static feign.Util.checkNotNull;
import static org.springframework.http.MediaType.*;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-30
 */
public interface HttpApiClient {

    SpelParserConfiguration SPEL_CONFIG = new SpelParserConfiguration(
            SpelCompilerMode.MIXED,
            ClassUtils.getDefaultClassLoader(),
            false,
            false,
            Integer.MAX_VALUE
    );
    SpelExpressionParser SPEL_PARSER = new SpelExpressionParser(SPEL_CONFIG);

    TemplateParserContext SPEL_TEMPLATE = new TemplateParserContext("${", "}");

    MediaType TEXT_HTML_UTF8 = new MediaType("text", "html", StandardCharsets.UTF_8);

    Map<HttpApiClient, Long> FREQUENCY_CONTROL = new HashMap<>();

    okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient.Builder()
            .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .connectionSpecs(Collections.singletonList(ConnectionSpec.MODERN_TLS))
            .proxy(Proxy.NO_PROXY)
            .build();

    WebClient CHROME_BROWSER = CHROME_BROWSER();

    static WebClient CHROME_BROWSER() {
        if (CHROME_BROWSER != null) {
            return CHROME_BROWSER;
        }
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.waitForBackgroundJavaScript(3000);
        webClient.setJavaScriptTimeout(3000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setTimeout(10000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        Runtime.getRuntime().addShutdownHook(new Thread("CHROME_BROWSER_ShutdownHook") {
            @Override
            public void run() {
                webClient.close();
            }
        });
        return webClient;
    }

    static <T extends HttpApiClient> T newClient(Class<T> clazz, String baseUrl,
                                                 HttpApiClientRequestInterceptor requestInterceptor) {
        Preconditions.checkArgument(clazz.isInterface(), clazz.getName() + " should be an interface");
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        stringHttpMessageConverter.setSupportedMediaTypes(Lists.newArrayList(TEXT_PLAIN, TEXT_HTML, TEXT_HTML_UTF8,
                APPLICATION_JSON, TEXT_XML, new MediaType("application", "*+json")));
        HttpMessageConverters messageConverters = new HttpMessageConverters(false, Lists.newArrayList(
                stringHttpMessageConverter,
                new ByteArrayHttpMessageConverter(),
                new ResourceHttpMessageConverter(false),
                new AllEncompassingFormHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter(JacksonUtil.JSON_MAPPER),
                new FastJsonHttpMessageConverter()
        ));
        SpringEncoder springEncoder = new SpringEncoder(() -> messageConverters);
        SpringDecoder springDecoder = new SpringDecoder(() -> messageConverters);
        return Feign.builder()
                .client(new OkHttpClient(httpClient))
                .encoder(springEncoder)
                .decoder(new HttpCompressDecoder(springDecoder))
                .requestInterceptor(requestInterceptor)
                .invocationHandlerFactory(ExecPointInvocationHandler::new)
                .options(new feign.Request.Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true))
                .retryer(new Retryer.Default(5000, 10000, 3))
                .target(clazz, baseUrl);
    }

    static boolean frequencyOk(HttpApiClient httpApiClient) {
        return frequencyOk(httpApiClient, 6);
    }

    static boolean frequencyOk(HttpApiClient httpApiClient, int minSecondsOnce) {
        long now = System.currentTimeMillis();
        long preSt = FREQUENCY_CONTROL.computeIfAbsent(httpApiClient, k -> now);
        if (System.currentTimeMillis() - preSt < new Random().nextInt(minSecondsOnce * 1000) + minSecondsOnce * 1000L) {
            return false;
        }
        FREQUENCY_CONTROL.put(httpApiClient, now);
        return true;
    }

    default void prepareHeaders(HttpApiRequestContext context) {
    }

    /**
     * @param supplier
     * @param <T>
     * @return
     * @throws Throwable
     * @see ExecPointInvocationHandler#invoke(Object, Method, Object[])
     */
    default <T> T execPoint(ExecSupplier<T> supplier) throws Throwable {
        HttpApiRequestContext context = HttpApiRequestContext.getCurrent();
        HttpApiClient oldClient = context.getHttpApiClient();
        context.setHttpApiClient(this);
        Map<String, String> oldHeaders = new LinkedHashMap<>(context.getCustomHeaders().size());
        oldHeaders.putAll(context.getCustomHeaders());
        context.getCustomHeaders().clear();
        try {
            return supplier.get();
        } finally {
            context.setHttpApiClient(oldClient);
            context.getCustomHeaders().clear();
            context.getCustomHeaders().putAll(oldHeaders);
        }
    }

    default boolean isHtmlRequest(RequestTemplate request) {
        Collection<String> contentType = request.headers().entrySet().stream()
                .filter(entry -> "Content-Type".equalsIgnoreCase(entry.getKey()))
                .map(Map.Entry::getValue).findFirst().orElse(null);
        return CollectionUtils.isNotEmpty(contentType) && StringUtils.isNotBlank(contentType.stream()
                .filter(c -> c.toLowerCase().contains("html")).findFirst().orElse(null));
    }

    default JSONObject checkJsonResult(ExecSupplier<JSONObject> supplier) throws Throwable {
        return supplier.get();
    }

    default HttpApiPageDataGripper getPageDataGripper() {
        return null;
    }

    default <T> List<T> pullPagedEntityItems(JSONObject query, FuConverter<JSONObject, T> converter,
                                             ExecSupplier<JSONObject> supplier) throws Throwable {
        return getPageDataGripper().processEntityItem(query, converter, () -> checkJsonResult(supplier));
    }

    @FunctionalInterface
    interface ExecSupplier<T> {
        T get() throws Throwable;
    }


    class ExecPointInvocationHandler implements InvocationHandler {

        private final Target target;
        private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;
        private final InvocationHandlerFactory.MethodHandler execPoint;
        private final Logger logger;
        private final Class<?> targetClass;
        private final DWH_DB dwhDb;
        private static final Map<String, MongoTemplate> DWH_DB = new HashMap<>();

        ExecPointInvocationHandler(Target target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
            this.target = checkNotNull(target, "target");
            this.dispatch = checkNotNull(dispatch, "dispatch for %s", target);
            this.execPoint = dispatch.entrySet().stream().filter(kv -> "execPoint".equals(kv.getKey().getName()))
                    .map(Map.Entry::getValue).findFirst().orElse(null);
            this.targetClass = target.type();
            this.dwhDb = AnnotationUtils.findAnnotation(targetClass, DWH_DB.class);
            this.logger = LoggerFactory.getLogger(targetClass);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("equals".equals(method.getName())) {
                try {
                    Object otherHandler = args.length > 0 && args[0] != null ?
                            java.lang.reflect.Proxy.getInvocationHandler(args[0]) : null;
                    return equals(otherHandler);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            } else if ("hashCode".equals(method.getName())) {
                return hashCode();
            } else if ("toString".equals(method.getName())) {
                return toString();
            }
            try {
                if (execPoint != null && !"execPoint".equals(method.getName())) {
                    RequestLine requestLine = AnnotationUtils.findAnnotation(method, RequestLine.class);
                    if (requestLine != null) {
                        return apiResultByDWH(method, args,
                                () -> execPoint.invoke(new ExecSupplier[]{() -> dispatch.get(method).invoke(args)}));
                    }
                }
                return dispatch.get(method).invoke(args);
            } catch (Throwable t) {
                StringBuilder strBuilder = new StringBuilder().append(targetClass.getSimpleName())
                        .append("#").append(method.getName()).append("(");
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    strBuilder.append("【{}】, ");
                    if (i == parameters.length - 1) {
                        strBuilder.append("【{}】");
                    }
                }
                strBuilder.append(")");
                logger.error(strBuilder.toString(), args);
                throw t;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ExecPointInvocationHandler other) {
                return target.equals(other.target);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public String toString() {
            return target.toString();
        }

        private Object apiResultByDWH(Method method, Object[] args, FuSupplier<Object> executor) throws Throwable {
            boolean ignore = dwhDb == null || dwhDb.ignore();
            if (ignore) {
                return executor.get();
            }
            String successEL = dwhDb.successEL();
            DWH_Collect dwhCollect = AnnotationUtils.getAnnotation(method, DWH_Collect.class);
            ignore = dwhCollect != null && dwhCollect.ignore();
            if (ignore) {
                return executor.get();
            }
            if (dwhCollect != null) {
                if (StringUtils.isNotBlank(dwhCollect.successEL())) {
                    successEL = dwhCollect.successEL();
                }
            }
            Preconditions.checkArgument(StringUtils.isNotBlank(successEL), "未设置接口请求成功判定");
            String dbName = dwhDb.value();
            if (StringUtils.isBlank(dbName)) {
                dbName = targetClass.getSimpleName();
            }
            String collectName = dwhCollect == null ? null : dwhCollect.value();
            if (StringUtils.isBlank(collectName)) {
                collectName = method.getName();
            }
            String databaseName = dbName;
            MongoTemplate mongoTemplate = DWH_DB.computeIfAbsent(dbName, k -> {
                MongoClient mongo = EntCreditBeanUnit.getBean(MongoClient.class);
                return new MongoTemplate(mongo, databaseName);
            });
            if (!mongoTemplate.collectionExists(collectName)) {
                mongoTemplate.createCollection(collectName);
            }
            Body body = AnnotationUtils.getAnnotation(method, Body.class);
            String jsonBodyParamName = null;
            if (body != null && body.value().contains("{") && body.value().contains("}")) {
                jsonBodyParamName = body.value().replaceAll("\\{", "");
                jsonBodyParamName = jsonBodyParamName.replaceAll("}", "").trim();
            }
            JSONArray paramArr;
            if (StringUtils.isBlank(jsonBodyParamName)) {
                paramArr = new JSONArray(Arrays.asList(args));
            } else {
                paramArr = new JSONArray(args.length);
                Parameter[] parameters = method.getParameters();
                boolean bodyExist = false;
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    Parameter parameter = parameters[i];
                    Param paramDesc = AnnotationUtils.getAnnotation(parameter, Param.class);
                    if (paramDesc != null && Objects.equals(jsonBodyParamName, paramDesc.value())) {
                        Preconditions.checkArgument(
                                arg != null, "%s#%s，未发现 @Body 定义的参数",
                                targetClass.getName(), method.getName()
                        );
                        bodyExist = true;
                        paramArr.add(JSON.parse(arg.toString()));
                    } else {
                        paramArr.add(args[i]);
                    }
                }
                Preconditions.checkArgument(
                        bodyExist, "%s#%s，未发现 @Body 定义的参数",
                        targetClass.getName(), method.getName()
                );
            }
            Criteria criteria = Criteria.where("param").is(paramArr);
            // query or save
            Query query = new Query(criteria);
            ApiMethod apiMethod = mongoTemplate.findOne(query, ApiMethod.class, collectName);
            boolean needUpdate = apiMethod == null || apiMethod.getResult() == null;
            if (!needUpdate) {
                Boolean success = SPEL_PARSER.parseExpression(successEL, SPEL_TEMPLATE)
                        .getValue(apiMethod, Boolean.class);
                success = success != null && success;
                needUpdate = !success;
            }
            if (needUpdate) {
                Object data = executor.get();
                if (data == null) {
                    return null;
                }
                mongoTemplate.remove(query, collectName);
                apiMethod = new ApiMethod();
                apiMethod.setParam(paramArr);
                apiMethod.setResult(data);
                apiMethod.setCreateTime(new Date());
                mongoTemplate.insert(apiMethod, collectName);
                mongoTemplate.indexOps(collectName).ensureIndex(ApiMethod.CREATE_TIME_ASC);
                mongoTemplate.indexOps(collectName).ensureIndex(ApiMethod.CREATE_TIME_DESC);
            }
            return TypeUtils.cast(apiMethod.getResult(), method.getReturnType());
        }
    }

    @Data
    class ApiMethod {
        static final Index CREATE_TIME_ASC = new Index("createTime", Sort.Direction.ASC).named("CREATE_TIME_ASC");
        static final Index CREATE_TIME_DESC = new Index("createTime", Sort.Direction.DESC).named("CREATE_TIME_DESC");
        private JSONArray param;
        private Object result;
        private Date createTime;
    }
}
