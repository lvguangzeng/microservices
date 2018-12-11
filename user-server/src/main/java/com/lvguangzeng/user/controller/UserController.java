package com.lvguangzeng.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Value("${env}")
    private String env;

    @GetMapping("/status")
    public String status() {
        return "status=OK \t " + env;
    }
}
