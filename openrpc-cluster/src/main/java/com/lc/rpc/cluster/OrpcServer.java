package com.lc.rpc.cluster;

/**
 * @author gujixian
 * @since 2023/11/20
 */
public interface OrpcServer {
    void startServer();

    void sendMsg(byte[] msg);
}
