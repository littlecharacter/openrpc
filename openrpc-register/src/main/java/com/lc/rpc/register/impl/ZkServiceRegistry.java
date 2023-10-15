package com.lc.rpc.register.impl;

import com.lc.rpc.common.Configuration;
import com.lc.rpc.common.Constant;
import com.lc.rpc.register.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZkServiceRegistry implements ServiceRegistry {
    private CuratorFramework zkClient;
    private List<String> serviceNodes = new ArrayList<>();

    public ZkServiceRegistry() {
        zkClient = CuratorFrameworkFactory.builder()
                // 格式：zk1:2181,zk2:2182,zk3:2183
                .connectString(Configuration.getProperty(Constant.OPENRPC_REGISTER_ADDRESS))
                .sessionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        zkClient.start();
    }

    @Override
    public void registry(String serviceName, String serviceAddress) {
        String servicePath = Constant.OPENRPC_REGISTRY_PATH + "/" + serviceName;
        try {
            if (zkClient.checkExists().forPath(servicePath) == null) {
                zkClient.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(servicePath, "0".getBytes());
                System.out.println("服务注册成功：" + servicePath);
            } else {
                System.out.println("现有注册服务：" + servicePath);
            }
            String addressPath = servicePath + "/" + serviceAddress;
            String node = zkClient.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(addressPath, "0".getBytes());
            System.out.println("服务节点创建成功：" + node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> discover(String serviceName) {
        if (!serviceNodes.isEmpty()) {
            System.out.println("服务(" + serviceName + ")节点：" + serviceNodes);
            return serviceNodes;
        }
        String servicePath = Constant.OPENRPC_REGISTRY_PATH + "/" + serviceName;
        try {
            serviceNodes = zkClient.getChildren().forPath(servicePath);
            System.out.println("服务(" + serviceName + ")节点：" + serviceNodes);
            final PathChildrenCache watcher = new PathChildrenCache(zkClient, servicePath, true);
            watcher.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            watcher.getListenable().addListener((client, event) -> {
                List<String> nodes = zkClient.getChildren().forPath(servicePath);
                if (nodes != null) {
                    serviceNodes.clear();
                    Collections.copy(serviceNodes, nodes);
                    System.out.println("服务(" + serviceName + ")节点变更：" + serviceNodes);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceNodes;
    }
}
