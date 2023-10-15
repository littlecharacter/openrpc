package com.lc.rpc.demo.client.service.impl;

import com.lc.rpc.demo.client.service.GreetService;
import com.lc.rpc.demo.contract.HelloService;
import com.lc.rpc.harbor.annotation.OrpcReference;
import org.springframework.stereotype.Service;

/**
 * @author gujixian
 * @since 2023/10/15
 */
@Service("greetService")
public class GreetServiceImpl implements GreetService {
    @OrpcReference
    private HelloService helloService;

    @Override
    public String greet(String name) {
        return helloService.sayHello(name);
    }
}
