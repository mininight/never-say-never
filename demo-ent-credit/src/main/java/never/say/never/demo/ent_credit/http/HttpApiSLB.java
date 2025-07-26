/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.http;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-09
 */
public class HttpApiSLB<T extends HttpApiSLB.Res> implements BiFunction<String, T, T> {

    private final List<BiFunction<String, T, T>> list;

    private final Map<String, AtomicInteger> loopCounter = new HashMap<>();

    private int coreIndex = 0;

    public HttpApiSLB(List<BiFunction<String, T, T>> list) {
        this.list = list;
    }

    @SafeVarargs
    public static <T extends HttpApiSLB.Res> HttpApiSLB<T> of(BiFunction<String, T, T>... functions) {
        return new HttpApiSLB<>(Arrays.asList(functions));
    }

    @Override
    public T apply(String execId, T t) {
        try {
            if (coreIndex >= list.size()) {
                coreIndex = 0;
                Preconditions.checkArgument(loopCounter
                                .computeIfAbsent(execId, k -> new AtomicInteger(0)).incrementAndGet() < 3,
                        "接口访问失败");
            }
            T res = list.get(coreIndex).apply(execId, t);
            if (res == null || !res.O_K()) {
                coreIndex++;
                res = apply(execId, t);
            }
            coreIndex++;
            return res;
        } finally {
            loopCounter.remove(execId);
        }
    }

    public interface Res {
        boolean O_K();
    }
}
