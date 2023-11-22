package com.lc.rpc.proxy;

import com.lc.rpc.cluster.OrpcServerPool;
import com.lc.rpc.protocol.MsgHead;
import com.lc.rpc.protocol.RequestBody;
import com.lc.rpc.remoting.OrpcServer;
import com.lc.rpc.remoting.callback.ClientCallback;
import com.lc.rpc.serializer.ObjectSerializer;
import com.lc.rpc.serializer.impl.KryoSerializer;
import io.netty.buffer.Unpooled;

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
                            RequestBody msgBody = new RequestBody();
                            msgBody.setClassName(clazz.getName());
                            msgBody.setMethodName(method.getName());
                            msgBody.setParamTypes(method.getParameterTypes());
                            msgBody.setParamValues(args);
                            ObjectSerializer serializer = new KryoSerializer();
                            byte[] bodyBytes = serializer.serialize(msgBody);
                            msgHead.setDataLength(bodyBytes.length);
                            // 3，注册回调
                            CompletableFuture<Object> cf = new CompletableFuture<>();
                            ClientCallback.addCallback(msgHead.getRequestId(), cf);
                            // 4，调用服务
                            server.sendMsg(Unpooled.wrappedBuffer(msgHead.getHead(), bodyBytes));
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
