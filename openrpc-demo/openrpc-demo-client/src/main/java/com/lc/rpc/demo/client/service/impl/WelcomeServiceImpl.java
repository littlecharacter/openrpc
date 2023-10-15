package com.lc.rpc.demo.client.service.impl;

import com.lc.rpc.demo.client.service.WelcomeService;
import com.lc.rpc.demo.contract.HelloService;
import com.lc.rpc.harbor.annotation.OrpcReference;
import org.springframework.stereotype.Service;

/**
 * @author gujixian
 * @since 2023/10/14
 */
@Service("welcomeService")
public class WelcomeServiceImpl implements WelcomeService {
    @OrpcReference
    private HelloService helloService;

    @Override
    public String welcome(String name) {
        return helloService.sayHello(name);
    }
}
