package com.lc.rpc.demo.client.controller;

import com.lc.rpc.demo.client.service.GreetService;
import com.lc.rpc.demo.client.service.WelcomeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author gujixian
 * @since 2023/10/14
 */
@Controller
@ResponseBody
public class WelcomeController {
    @Resource
    private WelcomeService welcomeService;
    @Resource
    private GreetService greetService;

    @RequestMapping("/welcome")
    public String welcome(String name) {
        return welcomeService.welcome(name);
    }

    @RequestMapping("/greet")
    public String greet(String name) {
        return greetService.greet(name);
    }
}
