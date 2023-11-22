package com.lc.rpc.remoting;

import com.lc.rpc.protocol.Message;

/**
 * @author gujixian
 * @since 2023/11/20
 */
public interface OrpcServer {
    void startServer();

    void sendMsg(Message<?> message);
}
