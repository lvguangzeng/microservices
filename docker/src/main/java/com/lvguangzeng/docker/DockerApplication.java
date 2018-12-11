package com.lvguangzeng.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DockerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerApplication.class, args);
        for (int i = 0; i < 100; i++) {
            System.out.println("当前时间" + System.currentTimeMillis());
        }
    }
}
