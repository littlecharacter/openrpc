package com.lc.rpc.demo.server;

import com.lc.rpc.annotation.EnableOrpc;
import com.lc.rpc.demo.contract.HelloService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @PropertySource(value = {"classpath:openrpc.properties"})
// @EnableOrpc
@EnableOrpc(path = "config/openrpc.properties")
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
		System.out.println(SpringUtil.getBean(HelloService.class));
	}

}
