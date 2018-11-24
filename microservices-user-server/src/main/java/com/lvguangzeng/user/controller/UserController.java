package com.lvguangzeng.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@GetMapping("/status")
	private String status() {
		return "status=OK";
	}
}
