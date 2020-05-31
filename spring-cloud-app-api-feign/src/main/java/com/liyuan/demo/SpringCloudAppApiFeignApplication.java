package com.liyuan.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//声明此服务是服务消费者
@EnableDiscoveryClient
//EnableFeignClients：表示开启Spring Cloud Feign的支持功能
@EnableFeignClients
public class SpringCloudAppApiFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudAppApiFeignApplication.class, args);
    }

}
