/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.util.TypeUtils;
import lombok.Data;
import never.say.never.demo.ent_credit.exceptions.HttpApiFailedException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分页数据抓取器
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2024-09-12
 */
@Data
public class HttpApiPageDataGripper {

    /**
     * 起始页
     */
    private Integer pageNoStart;

    /**
     * 页容量
     */
    private Integer pageSize;

    /**
     * 页码参数名称
     */
    private String pageNoArgName;

    /**
     * 页容量参数名称
     */
    private String pageSizeArgName;

    /**
     * 总页数提取器
     */
    private FuConverter<JSONObject, Integer> totalPageFetcher;

    /**
     * 分页数据提取器
     */
    private FuConverter<JSONObject, JSONArray> pageDataFetcher;

    public static HttpApiPageDataGripper newInstance() {
        return new HttpApiPageDataGripper();
    }

    public HttpApiPageDataGripper pageNoStart(Integer pageNoStart) {
        setPageNoStart(pageNoStart);
        return this;
    }

    public HttpApiPageDataGripper pageSize(Integer pageSize) {
        setPageSize(pageSize);
        return this;
    }

    public HttpApiPageDataGripper pageNoArgName(String pageNoArgName) {
        setPageNoArgName(pageNoArgName);
        return this;
    }

    public HttpApiPageDataGripper pageSizeArgName(String pageSizeArgName) {
        setPageSizeArgName(pageSizeArgName);
        return this;
    }

    public HttpApiPageDataGripper totalPageFetcher(FuConverter<JSONObject, Integer> totalPageFetcher) {
        setTotalPageFetcher(totalPageFetcher);
        return this;
    }

    public HttpApiPageDataGripper pageDataFetcher(FuConverter<JSONObject, JSONArray> pageDataFetcher) {
        setPageDataFetcher(pageDataFetcher);
        return this;
    }

    public <T> List<T> processArrayItem(JSONObject query, FuConverter<JSONArray, T> converter,
                                        FuSupplier<JSONObject> apiResultSupplier) throws Throwable {
        return doProcess(query, (result, object) -> {
            JSONArray item;
            if (object instanceof List) {
                item = TypeUtils.cast(object, JSONArray.class);
                T entity = converter.apply(item);
                result.add(entity);
            } else {
                throw new HttpApiFailedException(200, String.format("分页条目对象类型%s未受支持",
                        object.getClass().getName()));
            }
        }, apiResultSupplier);
    }

    public <T> List<T> processEntityItem(JSONObject query, FuConverter<JSONObject, T> converter,
                                         FuSupplier<JSONObject> apiResultSupplier) throws Throwable {
        return doProcess(query, (result, object) -> {
            JSONObject item;
            if (object instanceof Map) {
                item = TypeUtils.cast(object, JSONObject.class);
                T entity = converter.apply(item);
                result.add(entity);
            } else if (object instanceof List) {
                JSONArray subItems = TypeUtils.cast(object, JSONArray.class);
                if (subItems != null && !subItems.isEmpty()) {
                    for (Object subObject : subItems) {
                        if (!(subObject instanceof Map)) {
                            throw new HttpApiFailedException(200, String.format("分页条目二级对象类型%s未受支持",
                                    object.getClass().getName()));
                        }
                        item = TypeUtils.cast(subObject, JSONObject.class);
                        T entity = converter.apply(item);
                        result.add(entity);
                    }
                }
            } else {
                throw new HttpApiFailedException(200, String.format("分页条目对象类型%s未受支持",
                        object.getClass().getName()));
            }
        }, apiResultSupplier);
    }

    public <T> List<T> directProcess(JSONObject query, Class<T> itemType, FuSupplier<JSONObject> apiResultSupplier)
            throws Throwable {
        return customProcess(query, itemType, o1 -> o1, apiResultSupplier);
    }

    public <E, T> List<T> customProcess(JSONObject query, Class<E> itemType, FuConverter<E, T> converter,
                                        FuSupplier<JSONObject> apiResultSupplier) throws Throwable {
        return doProcess(query, (result, object) -> {
            E item;
            try {
                item = TypeUtils.cast(object, itemType);
            } catch (Exception e) {
                throw new HttpApiFailedException(200, String.format("分页条目对象类型%s未受支持",
                        object.getClass().getName()), e);
            }
            T entity = converter.apply(item);
            result.add(entity);
        }, apiResultSupplier);
    }

    public <T> List<T> doProcess(JSONObject query, BiFuConsumer<List<T>, Object> consumer,
                                 FuSupplier<JSONObject> apiResultSupplier) throws Throwable {
        int curPage = pageNoStart;
        query.put(pageNoArgName, curPage);
        query.put(pageSizeArgName, pageSize);
        JSONObject apiResult = apiResultSupplier.get();
        JSONArray list = pageDataFetcher.apply(apiResult);
        int totalPage = totalPageFetcher.apply(apiResult);
        JSONArray fullData = new JSONArray();
        while (CollectionUtils.isNotEmpty(list) && curPage <= totalPage) {
            fullData.addAll(list);
            apiResult = apiResultSupplier.get();
            list = pageDataFetcher.apply(apiResult);
            curPage++;
            query.put(pageNoArgName, curPage);
        }
        if (CollectionUtils.isEmpty(fullData)) {
            return new ArrayList<>();
        }
        int size = fullData.size();
        List<T> result = new ArrayList<>(size);
        for (Object itemObject : fullData) {
            if (itemObject == null) {
                continue;
            }
            consumer.accept(result, itemObject);
        }
        return result;
    }
}
