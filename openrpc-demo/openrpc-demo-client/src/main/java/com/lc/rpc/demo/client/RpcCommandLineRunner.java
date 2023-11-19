package com.lc.rpc.demo.client;

import com.lc.rpc.demo.client.service.WelcomeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author gujixian
 * @since 2023/11/20
 */
@Component
public class RpcCommandLineRunner implements CommandLineRunner {
    @Resource
    private WelcomeService welcomeService;

    @Override
    public void run(String... args) {
        System.out.println(welcomeService.welcome("wangwu"));
    }
}