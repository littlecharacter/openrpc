package com.lc.rpc.demo.server;

import com.lc.rpc.demo.contract.HelloService;

/**
 * @author gujixian
 * @since 2023/10/14
 */
public class HelloServiceServer implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hi," + name;
    }
}
