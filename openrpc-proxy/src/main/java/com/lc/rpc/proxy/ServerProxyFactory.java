package com.lc.rpc.proxy;

import com.lc.rpc.cluster.OrpcServerPool;
import com.lc.rpc.common.Configuration;
import com.lc.rpc.common.Constant;
import com.lc.rpc.protocol.Message;
import com.lc.rpc.protocol.MsgHead;
import com.lc.rpc.protocol.RequestBody;
import com.lc.rpc.protocol.ResponseBody;
import com.lc.rpc.register.ServiceRegister;
import com.lc.rpc.register.impl.ZkServiceRegister;
import com.lc.rpc.remoting.callback.ServerCallback;
import com.lc.rpc.serializer.ObjectSerializer;
import com.lc.rpc.serializer.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
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
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        // 2，启动服务端
        OrpcServerPool.buildServer(new InetSocketAddress(address, Integer.parseInt(Configuration.getProperty(Constant.OPENRPC_SERVICE_PORT))), new BizServerCallback());

        String serviceAddress = address.getHostAddress() + ":" + Configuration.getProperty(Constant.OPENRPC_SERVICE_PORT);
        ServiceRegister register = new ZkServiceRegister();
        beansWithAnnotation.forEach((beanName, bean) -> {
            Class<?> clazz = bean.getClass();
            for (Class<?> face : clazz.getInterfaces()) {
                // 2，创建代理
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

                // 3，服务注册
                register.registry(face.getName(), serviceAddress, "providers");
            }
        });
    }

    public static Invoker getProxy(String className) {
        return proxyMap.get(className);
    }

    private static class BizServerCallback implements ServerCallback {
        @Override
        public ByteBuf call(Message<RequestBody> message) {
            MsgHead msgHead = message.getMsgHead();
            RequestBody requestBody = message.getMsgBody();
            Invoker invoker = getProxy(requestBody.getClassName());
            InvokerResult invokerResult = invoker.invoke(requestBody.getMethodName(), requestBody.getParamTypes(), requestBody.getParamValues());
            // TODO: 2023/10/21 状态待处理
            ResponseBody responseBody = new ResponseBody();
            responseBody.setResult(invokerResult.getResult());
            ObjectSerializer serializer = new KryoSerializer();
            byte[] responseBytes = serializer.serialize(responseBody);
            msgHead.setFlag((byte) -1);
            msgHead.setDataLength(responseBytes.length);
            return Unpooled.wrappedBuffer(msgHead.getHead(), responseBytes);
        }
    }
}
