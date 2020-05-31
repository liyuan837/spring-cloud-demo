package com.liyuan.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
//@EnableCaching
@EnableDiscoveryClient
//声明为保护资源
//@EnableResourceServer
public class SpringCloudProviderUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudProviderUserApplication.class, args);
    }

}
