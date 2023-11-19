package com.lc.rpc.register.impl;

import com.lc.rpc.common.Configuration;
import com.lc.rpc.common.Constant;
import com.lc.rpc.register.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZkServiceRegistry implements ServiceRegistry {
    private CuratorFramework zkClient;
    private Map<String, List<String>> nodeMap = new HashMap<>();

    public ZkServiceRegistry() {
        zkClient = CuratorFrameworkFactory.builder()
                // 格式：zk1:2181,zk2:2181,zk3:2181
                .connectString(Configuration.getProperty(Constant.OPENRPC_REGISTER_ADDRESS))
                .sessionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        zkClient.start();
    }

    @Override
    public void registry(String serviceName, String serviceAddress, String role) {
        String servicePath = Constant.OPENRPC_REGISTRY_PATH + "/services/" + serviceName;
        try {
            System.out.println(zkClient.getChildren().forPath("/"));
            // 创建服务节点
            if (zkClient.checkExists().forPath(servicePath) == null) {
                zkClient.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(servicePath, (Configuration.getProperty(Constant.OPENRPC_SERVICE_NAME) + "").getBytes());
                System.out.println("服务注册成功：" + servicePath);
            } else {
                zkClient.setData().forPath(servicePath, (Configuration.getProperty(Constant.OPENRPC_SERVICE_NAME) + "").getBytes());
                System.out.println("现有注册服务：" + servicePath);
            }
            // 创建角色节点：providers、configurators、consumers
            String rolePath = servicePath + "/" + role;
            if (zkClient.checkExists().forPath(rolePath) == null) {
                zkClient.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(rolePath);
                System.out.println("服务角色创建成功：" + rolePath);
            }
            // 创建服务节点
            String addressPath = rolePath + "/" + serviceAddress;
            if (zkClient.checkExists().forPath(addressPath) == null) {
                String node = zkClient.create()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(addressPath);
                System.out.println("服务节点创建成功：" + node);
            } else {
                System.out.println("服务节点已存在：" + addressPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> discover(String serviceName) {
        if (nodeMap.containsKey(serviceName)) {
            List<String> serviceNodes = nodeMap.get(serviceName);
            System.out.println("服务(" + serviceName + ")节点：" + serviceNodes);
            return serviceNodes;
        }
        String nodePath = Constant.OPENRPC_REGISTRY_PATH + "/services/" + serviceName + "/providers";
        try {
            List<String> serviceNodes = zkClient.getChildren().forPath(nodePath);
            System.out.println("服务(" + serviceName + ")节点：" + serviceNodes);
            nodeMap.put(serviceName, serviceNodes);
            // TODO: 2023/11/19 反向通知是否有必要？
            final PathChildrenCache watcher = new PathChildrenCache(zkClient, nodePath, true);
            watcher.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            watcher.getListenable().addListener((client, event) -> {
                List<String> nodes = zkClient.getChildren().forPath(nodePath);
                if (nodes != null) {
                    nodeMap.put(serviceName, nodes);
                    System.out.println("服务(" + serviceName + ")节点变更：" + nodes);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodeMap.get(serviceName);
    }

    // public static void main(String[] args) throws Exception {
    //     Properties properties = new Properties();
    //     properties.setProperty(Constant.OPENRPC_SERVICE_NAME, "com.lc.study.demo");
    //     properties.setProperty(Constant.OPENRPC_REGISTER_ADDRESS, "zk:2181,zk:2182,zk:2183");
    //     Configuration.setProperties(properties);
    //
    //     ZkServiceRegistry register = new ZkServiceRegistry();
    //     register.registry("com.lc.rpc.demo.contract.HelloService", "192.168.1.9:10926", "providers");
    //
    //     System.out.println(register.discover("com.lc.rpc.demo.contract.HelloService"));
    //
    //     TimeUnit.SECONDS.sleep(30);
    // }
}
