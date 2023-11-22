package com.lc.rpc.remoting.callback;

import com.lc.rpc.protocol.Message;
import com.lc.rpc.protocol.ResponseBody;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gujixian
 * @since 2023/11/22
 */
public final class ClientCallback {
    private static final Map<Long, CompletableFuture<Object>> CALLBACK_CENTER = new ConcurrentHashMap<>();

    private ClientCallback() {}

    public static void addCallback(long requestId, CompletableFuture<Object> cf) {
        CALLBACK_CENTER.putIfAbsent(requestId, cf);
    }

    public static void runCallback(Message<?> message) {
        long requestId = message.getMsgHead().getRequestId();
        CompletableFuture<Object> cf = CALLBACK_CENTER.get(requestId);
        cf.complete(((ResponseBody) message.getMsgBody()).getResult());
        CALLBACK_CENTER.remove(requestId);
    }
}
