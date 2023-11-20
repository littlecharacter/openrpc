package com.lc.rpc.proxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gujixian
 * @since 2023/11/20
 */
public final class ServerProxyFactory {
    private static ConcurrentHashMap<String, Invoker> proxyMap = new ConcurrentHashMap<>();

    private ServerProxyFactory() {}

    // TODO: 2023/10/21 这里可以使用 javassist 代理，避免反射调用 -> 见设计模式之动态代理
    public static void build(Map<String, Object> beansWithAnnotation) {
        beansWithAnnotation.forEach((beanName, bean) -> {
            Class<?> clazz = bean.getClass();
            for (Class<?> face : clazz.getInterfaces()) {
                System.out.println(face.getName());
                proxyMap.putIfAbsent(face.getName(), new Invoker() {
                    @Override
                    public InvokerResult invoke(String methodName, Class<?>[] paramTypes, Object[] paramValues) {
                        InvokerResult result = new InvokerResult();
                        try {
                            Method method = face.getMethod(methodName, paramTypes);
                            Object object = method.invoke(bean, paramValues);
                            result.setStatusCode(200);
                            result.setStatusDesc("SUCCESS");
                            result.setResult(object);
                        } catch (Exception e) {
                            result.setStatusCode(500);
                            result.setStatusDesc(e.getMessage());
                        }
                        return result;
                    }
                });
            }
        });
    }

    public static Invoker getProxy(String className) {
        return proxyMap.get(className);
    }
}
