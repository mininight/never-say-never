/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.util;


import lombok.Getter;
import org.springframework.cglib.proxy.*;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-09-20
 */
public class SimpleProxy {

    private static final CallbackFilter BAD_OBJECT_METHOD_FILTER = method -> {
        String methodName = method.getName();
        if (ReflectionUtils.isObjectMethod(method) || "finalize".equals(methodName) || "clone".equals(methodName)
                || "main".equals(methodName)) {
            return 1;
        }
        return 0;
    };

    public static <T> T newInstance(Class<T> clazz, MethodInvocationHandler handler) {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        Set<Class<?>> interfaces = getAllInterfacesForClassAsSet(clazz, classLoader);
        if (clazz.isInterface()) {
            Object proxyObj = java.lang.reflect.Proxy.newProxyInstance(classLoader,
                    interfaces.toArray(new Class<?>[0]),
                    (proxy, method, args) -> {
                        ActionPoint actionPoint = new ActionPoint(proxy, method, method);
                        return handler.invoke(proxy, actionPoint, args);
                    });
            return clazz.cast(proxyObj);
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setUseFactory(false);
        enhancer.setUseCache(true);
        enhancer.setInterceptDuringConstruction(false);
        enhancer.setClassLoader(classLoader);
        enhancer.setSuperclass(clazz);
        enhancer.setInterfaces(interfaces.toArray(new Class[0]));
        enhancer.setCallbacks(new Callback[]{(MethodInterceptor) (proxy, method, args, methodProxy) -> {
            ActionPoint point = new ActionPoint(proxy, methodProxy, method);
            return handler.invoke(proxy, point, args);
        }, NoOp.INSTANCE});
        enhancer.setCallbackFilter(BAD_OBJECT_METHOD_FILTER);
        return clazz.cast(enhancer.create());
    }

    public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz, @Nullable ClassLoader classLoader) {
        return getAllInterfacesForClassAsSet(clazz, classLoader, true);
    }

    public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz, @Nullable ClassLoader classLoader,
                                                              boolean withSelf) {
        Assert.notNull(clazz, "Class must not be null");
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        if (withSelf && clazz.isInterface() && ClassUtils.isVisible(clazz, classLoader)) {
            interfaces.add(clazz);
        }
        Class<?> current = clazz;
        while (current != null) {
            Class<?>[] ifcs = current.getInterfaces();
            for (Class<?> ifc : ifcs) {
                if (ClassUtils.isVisible(ifc, classLoader)) {
                    interfaces.add(ifc);
                }
            }
            current = current.getSuperclass();
        }
        return interfaces;
    }

    public interface MethodInvocationHandler {
        Object invoke(Object proxy, ActionPoint actionPoint, Object[] args) throws Throwable;
    }

    public enum ProxyMode {
        JDK,
        CGLIB,
        ;

        public static ProxyMode of(Object proxyObj) {
            if (proxyObj instanceof java.lang.reflect.Proxy) {
                return JDK;
            }
            if (proxyObj.getClass().getName().contains("CGLIB$")) {
                return CGLIB;
            }
            throw new UnsupportedOperationException(String.format("未受支持的代理类型: %s",
                    proxyObj.getClass().getName()));
        }
    }

    @Getter
    public static class ActionPoint {

        private final Object proxy;

        private final ProxyMode proxyMode;

        private final Object superCall;

        private final Method method;


        public ActionPoint(Object proxy, Object superCall, Method method) {
            this.proxy = proxy;
            this.proxyMode = ProxyMode.of(proxy);
            this.superCall = superCall;
            this.method = method;
            ReflectionUtils.makeAccessible(this.method);
        }

        public Object invoke(Object obj, Object[] args) throws Throwable {
            if (proxy != obj) {
                return method.invoke(obj, args);
            }
            switch (proxyMode) {
                case JDK -> {
                    if (method.isDefault()) {
                        return InvocationHandler.invokeDefault(proxy, method, args);
                    } else if (ReflectionUtils.isObjectMethod(method)) {
                        return ReflectionUtils.invokeMethod(method, this);
                    } else {
                        return method.invoke(proxy, args);
                    }
                }
                case CGLIB -> {
                    return ((MethodProxy) superCall).invokeSuper(obj, args);
                }
                default -> throw new UnsupportedOperationException(String.format("未受支持的代理类型: %s",
                        proxy.getClass().getName()));
            }
        }
    }
}
