package com.lc.rpc.proxy;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public interface Invoker {
    InvokerResult invoke(String methodName, Class<?>[] paramTypes, Object[] paramValues);
}
