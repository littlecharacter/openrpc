import com.alibaba.fastjson.JSON;
import com.lc.rpc.serializer.ObjectSerializer;
import com.lc.rpc.serializer.impl.KryoSerializer;

import java.io.Serializable;

/**
 * @author gujixian
 * @since 2023/11/20
 */
public class TEST {
    public static void main(String[] args) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1234567890L);
        userInfo.setName("tom");
        userInfo.setAge(18);
        ObjectSerializer serializer = new KryoSerializer();
        System.out.println(serializer.serialize(userInfo).length);
        System.out.println(JSON.toJSONString(serializer.deserialize(serializer.serialize(userInfo))));
    }

    private static class UserInfo implements Serializable {
        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        private Integer age;
    }
}
