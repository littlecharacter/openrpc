package com.lc.rpc.serializer;

/**
 * @author gujixian
 * @since 2023/10/15
 */
public interface ObjectSerializer {
    byte[] serialize(Object obj);
    <T> T deserialize(byte[] bytes);
}
