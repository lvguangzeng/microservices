package com.lvguangzeng.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lvguangzeng.feign.UserFeignClient;

@RestController
@EnableEurekaClient
@EnableFeignClients("com.lvguangzeng.feign")
@SpringBootApplication
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

	@Autowired
	UserFeignClient userFeignClient;

	@GetMapping("/test")
	private String test() {
		return userFeignClient.status();
	}
}
