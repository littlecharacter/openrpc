package com.lc.rpc.proxy;

import com.lc.rpc.cluster.OrpcServerPool;
import com.lc.rpc.protocol.Message;
import com.lc.rpc.protocol.MsgHead;
import com.lc.rpc.protocol.RequestBody;
import com.lc.rpc.remoting.OrpcServer;
import com.lc.rpc.remoting.callback.ClientCallback;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ClientProxyFactory {
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
                            OrpcServer server = OrpcServerPool.getClientServer(clazz.getName());
                            // 2，构造请求
                            MsgHead msgHead = new MsgHead();
                            msgHead.setMagicHigh((byte) 1);
                            msgHead.setMagicLow((byte) 2);
                            msgHead.setFlag((byte) 0);
                            msgHead.setRequestId(Snowflake.instance().getId());
                            RequestBody requestBody = new RequestBody();
                            requestBody.setClassName(clazz.getName());
                            requestBody.setMethodName(method.getName());
                            requestBody.setParamTypes(method.getParameterTypes());
                            requestBody.setParamValues(args);
                            // 3，注册回调
                            CompletableFuture<Object> cf = new CompletableFuture<>();
                            ClientCallback.addCallback(msgHead.getRequestId(), cf);
                            // 4，调用服务
                            int retryTimes = 3;
                            while (true) {
                                try {
                                    server.sendMsg(new Message<>(msgHead, requestBody));
                                    break;
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                    server = OrpcServerPool.getClientServer(clazz.getName());
                                    retryTimes--;
                                    if (retryTimes <= 0) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                            // 5，获取结果
                            return cf.get();
                        }
                    });
        }
        if (proxy != null) {
            proxyMap.put(serviceName, proxy);
        }
        return (T) proxy;
    }
}
