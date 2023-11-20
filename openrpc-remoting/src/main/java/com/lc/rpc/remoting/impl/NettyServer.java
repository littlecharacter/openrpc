package com.lc.rpc.remoting.impl;

import com.lc.rpc.remoting.OrpcServer;

import java.net.InetSocketAddress;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public class NettyServer implements OrpcServer {
    private InetSocketAddress address;

    public NettyServer(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public void startServer() {

    }

    @Override
    public void sendMsg(byte[] msg) {

    }
}
