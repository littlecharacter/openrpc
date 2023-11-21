package com.lc.rpc.remoting.impl;

import com.lc.rpc.remoting.OrpcServer;
import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public abstract class AbstractServer implements OrpcServer {
    InetSocketAddress address;

    public AbstractServer(InetSocketAddress address) {
        this.address = address;
        startServer();
    }

    @Override
    public void startServer() {}

    @Override
    public void sendMsg(ByteBuf msgBuf) {}
}
