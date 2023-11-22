package com.lc.rpc.remoting.impl;

import com.lc.rpc.protocol.Message;
import com.lc.rpc.remoting.OrpcServer;

import java.net.InetSocketAddress;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public abstract class AbstractServer implements OrpcServer {
    InetSocketAddress address;

    public AbstractServer(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public void startServer() {}

    @Override
    public void sendMsg(Message<?> message) {}
}
