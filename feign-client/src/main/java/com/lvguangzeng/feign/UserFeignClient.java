package com.lvguangzeng.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("microservices-user-server")
public interface UserFeignClient {

	@GetMapping("/status")
	public String status();
}
