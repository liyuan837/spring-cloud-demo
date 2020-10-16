package com.liyuan.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableCaching
@EnableDiscoveryClient
//声明为保护资源
//@EnableResourceServer
public class ZookeeperProviderUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperProviderUserApplication.class, args);
    }

}
