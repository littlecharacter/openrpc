package com.lc.rpc.demo.server;

import com.lc.rpc.annotation.OrpcService;
import com.lc.rpc.demo.contract.HelloService;

/**
 * @author gujixian
 * @since 2023/10/14
 */
@OrpcService
public class HelloServiceServer implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hello," + name;
    }
}
