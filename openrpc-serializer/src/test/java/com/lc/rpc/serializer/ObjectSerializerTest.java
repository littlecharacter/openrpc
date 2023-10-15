package com.lc.rpc.serializer;

import com.alibaba.fastjson.JSON;
import com.lc.rpc.serializer.impl.KryoSerializer;
import org.junit.Test;

/**
 * @author gujixian
 * @since 2023/10/15
 */
public class ObjectSerializerTest {
    private ObjectSerializer serializer = new KryoSerializer();

    @Test
    public void serialize() {
        User user = new User();
        user.id = 20231015232653L;
        user.name = "xx";
        System.out.println(JSON.toJSONString(serializer.serialize(user)));
    }

    @Test
    public void deserialize() {
        User user = new User();
        user.id = 20231015232653L;
        user.name = "yy";
        System.out.println(JSON.toJSONString(serializer.deserialize(serializer.serialize(user))));
    }

    private static class User {
        private long id;
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}