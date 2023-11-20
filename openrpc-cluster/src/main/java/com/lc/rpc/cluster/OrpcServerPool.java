package com.lc.rpc.cluster;

import com.lc.rpc.remoting.OrpcServer;
import com.lc.rpc.remoting.impl.NettyServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gujixian
 * @since 2023/11/20
 */
public class OrpcServerPool {
    private static final Map<String, OrpcServer> ssMap = new ConcurrentHashMap<>();
    private static final Map<String, List<OrpcServer>> csMap = new ConcurrentHashMap<>();

    // 构建Server
    public static void buildServer(String serviceName, InetSocketAddress address) {
        ssMap.putIfAbsent(serviceName, new NettyServer(address));
    }

    // 负载均衡
    public static OrpcServer getServer(String serviceName) {
        if (!csMap.containsKey(serviceName)) {
            // 注册中心获取服务列表
            // 遍历构建 OrpcServer 并启动
            List<OrpcServer> serverList = new ArrayList<>();
            csMap.put(serviceName, serverList);
        }
        return csMap.get(serviceName).get(new Random().nextInt(csMap.get(serviceName).size()));    }

    // TODO: 2023/10/21  定时拉取注册中心，更新 csMap
}
