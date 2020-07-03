package com.liyuan.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudBusAmqpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudBusAmqpApplication.class, args);
    }

}
