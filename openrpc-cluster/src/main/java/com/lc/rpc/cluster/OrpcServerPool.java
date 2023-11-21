package com.lc.rpc.cluster;

import com.lc.rpc.register.ServiceRegister;
import com.lc.rpc.register.impl.ZkServiceRegister;
import com.lc.rpc.remoting.OrpcServer;
import com.lc.rpc.remoting.callback.ServerCallback;
import com.lc.rpc.remoting.impl.NettyClient;
import com.lc.rpc.remoting.impl.NettyServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author gujixian
 * @since 2023/11/20
 */
public class OrpcServerPool {
    private static final Map<String, List<OrpcServer>> csMap = new ConcurrentHashMap<>();
    private static volatile OrpcServer serverServer;

    // 构建Server
    public static void buildServer(InetSocketAddress address, ServerCallback serverCallback) {
        if (serverServer == null) {
            synchronized (OrpcServer.class) {
                if (serverServer == null) {
                    serverServer = new NettyServer(address, serverCallback);
                }
            }
        }
    }

    // TODO: 2023/11/21 负载均衡
    public static OrpcServer getClientServer(String serviceName) {
        if (!csMap.containsKey(serviceName)) {
            // 注册中心获取服务列表
            ServiceRegister register = new ZkServiceRegister();
            List<String> discover = register.discover(serviceName);
            // 遍历构建 OrpcServer 并启动
            CountDownLatch latch = new CountDownLatch(discover.size());
            List<OrpcServer> serverList = new ArrayList<>();
            for (String addressString : discover) {
                String[] address = addressString.split(":");
                serverList.add(new NettyClient(new InetSocketAddress(address[0], Integer.parseInt(address[1])), latch));
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            csMap.put(serviceName, serverList);
        }
        return csMap.get(serviceName).get(new Random().nextInt(csMap.get(serviceName).size()));    }

    // TODO: 2023/10/21  定时拉取注册中心，更新 csMap
}
