package com.lc.rpc.remoting.callback;

import com.lc.rpc.protocol.Message;
import com.lc.rpc.protocol.RequestBody;
import io.netty.buffer.ByteBuf;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public interface ServerCallback {
    ByteBuf call(Message<RequestBody> message);
}
