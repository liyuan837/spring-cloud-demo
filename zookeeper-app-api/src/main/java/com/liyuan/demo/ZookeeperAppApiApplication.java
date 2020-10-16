package com.liyuan.demo;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
//声明此服务是服务消费者
@EnableDiscoveryClient
//开启断路器功能
@EnableCircuitBreaker
public class ZookeeperAppApiApplication {

    //实现客户端负载均衡
    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    //更换客户端负载均衡策略，也可以在配置文件中指定服务的负载均衡策略
    @Bean
    public IRule iRule(){
        return new RandomRule();
    }


    public static void main(String[] args) {
        SpringApplication.run(ZookeeperAppApiApplication.class, args);
    }

}
