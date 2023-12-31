package com.lc.rpc.register;

import java.util.List;

public interface ServiceRegister {
    /**
     * 服务注册
     * @param serviceName 服务名称
     * @param serviceAddress ip:port
     */
    void registry(String serviceName, String serviceAddress, String role);

    /**
     * 服务发现
     * @param serviceName 服务名称
     * @return
     */
    List<String> discover(String serviceName);
}
