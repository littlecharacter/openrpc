package com.lc.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

public class OrpcProxyFactory {
    private static ConcurrentHashMap<String, Object> proxyMap = new ConcurrentHashMap<>();

    public static <T> T create(Class<?> clazz) {
        String serviceName = clazz.getName();
        System.out.println("serviceName：" + serviceName);
        Object proxy;
        if (proxyMap.containsKey(serviceName)) {
            proxy = proxyMap.get(serviceName);
        } else {
            proxy = Proxy.newProxyInstance(clazz.getClassLoader(),
                    new Class<?>[]{clazz}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            // 1，获取服务 - cluster
                            // 2，构造请求
                            // 3，调用服务
                            // 4，获取结果
                            return "hi," + args[0];
                        }
                    });
        }
        if (proxy != null) {
            proxyMap.put(serviceName, proxy);
        }
        return (T) proxy;
    }
}
