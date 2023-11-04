package com.lc.rpc.demo.server;

import com.lc.rpc.harbor.annotation.EnableOrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @PropertySource(value = {"classpath:openrpc.properties"})
// @EnableOrpc
@EnableOrpc(path = "config/openrpc.properties")
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

}
