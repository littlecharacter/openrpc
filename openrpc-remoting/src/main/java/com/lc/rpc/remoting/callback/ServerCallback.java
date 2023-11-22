package com.lc.rpc.remoting.callback;

import com.lc.rpc.protocol.Message;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public interface ServerCallback {
    Message<?> call(Message<?> message);
}
