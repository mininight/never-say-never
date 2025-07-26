/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.util;


import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-09-20
 */
public class SimpleSLB<T> {

    private final List<Srv<T>> srvList;

    private final Class<T> srvType;

    private final AtomicInteger itemIndex = new AtomicInteger(0);

    private final ReentrantLock lock = new ReentrantLock();

    public SimpleSLB(Class<T> srvType, List<T> srvList) {
        this.srvType = srvType;
        srvList.removeIf(Objects::isNull);
        this.srvList = srvList.stream().map(Srv::of).collect(Collectors.toList());
        System.out.println();
    }

    public static <E> E newInstance(Class<E> srvType, List<E> srvList, RouteSelector<E> routeSelector) {
        return new SimpleSLB<>(srvType, srvList).newInstance(routeSelector);
    }

    public T newInstance(RouteSelector<T> routeSelector) {
        return newInstance(2, routeSelector);
    }

    public T newInstance(int maxLoopCount, RouteSelector<T> routeSelector) {
        return SimpleProxy.newInstance(srvType, (proxy, point, args) -> {
            Class<?> targetClazz = point.getMethod().getDeclaringClass();
            String methodName = point.getMethod().getName();
            boolean doNext = true;
            int loopCount = 0;
            Object result = null;
            while (doNext) {
                if (loopCount >= maxLoopCount) {
                    throw new IllegalAccessException(String.format("Failed execute %s#%s after %s attempts",
                            targetClazz.getName(), methodName, maxLoopCount));
                }
                int curIndex = itemIndex.get();
                if (curIndex >= srvList.size()) {
                    if (resetIndex()) {
                        loopCount++;
                    }
                    curIndex = 0;
                }
                Srv<T> srv = srvList.get(curIndex);
                if (!srv.isValid()) {
                    itemIndex.getAndIncrement();
                } else {
                    RouteSelector.Decision decision = routeSelector == null ? RouteSelector.Decision.ACCEPT
                            : Objects.requireNonNull(
                            routeSelector.beforeCall(srv, curIndex, point.getMethod(), args),
                            "SLB route selector decision cannot be null (beforeCall)"
                    );
                    Throwable err = null;
                    try {
                        if (decision == RouteSelector.Decision.ACCEPT) {
                            result = point.invoke(srv.getObject(), args);
                        }
                    } catch (Throwable e) {
                        err = e;
                        srv.getLog().warn("", err);
                    } finally {
                        doNext = err != null;
                        if (!doNext) {
                            decision = routeSelector == null ? RouteSelector.Decision.ACCEPT
                                    : Objects.requireNonNull(
                                    routeSelector.afterCall(srv, curIndex, point.getMethod(), args, result),
                                    "SLB route selector decision cannot be null (afterCall)"
                            );
                            doNext = RouteSelector.Decision.NEXT == decision;
                        }
                        if (doNext) {
                            itemIndex.getAndIncrement();
                        }
                    }
                }
            }
            return result;
        });
    }

    private boolean resetIndex() {
        int tryCount = 0;
        int curIndex;
        do {
            curIndex = itemIndex.get();
            if (curIndex < srvList.size()) {
                return false;
            }
            boolean success = itemIndex.compareAndSet(curIndex, 0);
            tryCount++;
            if (success) {
                return true;
            }
        } while (tryCount < 20);
        //
        if (itemIndex.get() >= srvList.size()) {
            try {
                if (lock.tryLock(10, TimeUnit.SECONDS)) {
                    if (itemIndex.get() >= srvList.size()) {
                        itemIndex.set(0);
                        return true;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    @Data
    public static class Srv<S> {
        private final Logger log;
        private final S object;
        private boolean valid;
        private final JSONObject extension = new JSONObject();

        public Srv(S object) {
            this.log = LoggerFactory.getLogger(object.getClass());
            this.object = object;
            this.valid = true;
        }

        public static <V> Srv<V> of(V object) {
            return new Srv<>(object);
        }
    }

    @FunctionalInterface
    public interface RouteSelector<T> {

        Decision beforeCall(Srv<T> srv, int srvIndex, Method method, Object[] args)
                throws Throwable;

        default Decision afterCall(Srv<T> srv, int srvIndex, Method method, Object[] args, Object result)
                throws Throwable {
            return Decision.ACCEPT;
        }

        enum Decision {
            NEXT,
            ACCEPT
        }
    }
}
