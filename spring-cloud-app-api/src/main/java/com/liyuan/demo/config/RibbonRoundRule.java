package com.liyuan.demo.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RibbonRoundRule {

    //更换客户端负载均衡策略，也可以在配置文件中指定服务的负载均衡策略
    @Bean
    public IRule iRule(){
        return new RoundRobinRule();
    }

}
