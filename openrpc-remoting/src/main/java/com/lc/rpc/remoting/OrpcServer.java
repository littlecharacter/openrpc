package com.lc.rpc.remoting;

import io.netty.buffer.ByteBuf;

/**
 * @author gujixian
 * @since 2023/11/20
 */
public interface OrpcServer {
    void startServer();

    void sendMsg(ByteBuf msgBuf);
}
