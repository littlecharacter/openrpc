package com.lc.rpc.demo.client;

import com.lc.rpc.annotation.EnableOrpcConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @PropertySource(value = {"classpath:openrpc.properties"})
@EnableOrpcConsumer(path = "config/openrpc.properties")
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

}
