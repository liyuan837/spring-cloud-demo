# 说明

## 本项目示例：服务消费者——api-feign
> SpringBoot项目，服务消费者，消费spring-cloud-provider-user提供的服务
  
### 一、项目说明
1. 项目使用feign实现服务调用的客户端负载均衡和熔断功能
2. Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，可使用Feign 注解和JAX-RS注解。Feign支持可插拔的编码器和解码器。Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。
    - Feign 采用的是基于接口的注解
    - Feign 整合了ribbon，具有负载均衡的能力
    - Feign 整合了Hystrix，具有熔断的能力
     
### 二、项目启动
1. 引入openfeign依赖，如果后面需要接入hystrix.stream监控，则需要额外引入hystrix监控
    ```pom
                    <dependency>
                        <groupId>org.springframework.cloud</groupId>
                        <artifactId>spring-cloud-starter-openfeign</artifactId>
                    </dependency>
                  <!-- 服务消费者中加入断路器 hystrix监控-->
                  <dependency>
                      <groupId>org.springframework.cloud</groupId>
                      <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
                  </dependency>
    ```
2. 配置文件application.yml开启feign的熔断器开关
    ```yaml
          # feign相关配置
          feign:
            # 开启熔断，默认为false
            hystrix:
              enabled: true
    ```
3. 启动类添加相关注解
    ```java
      package com.liyuan.demo;
      
      import org.springframework.boot.SpringApplication;
      import org.springframework.boot.autoconfigure.SpringBootApplication;
      import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
      import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
      import org.springframework.cloud.openfeign.EnableFeignClients;
      
      @SpringBootApplication
      //声明此服务是服务消费者
      @EnableDiscoveryClient
      //EnableFeignClients：表示开启Spring Cloud Feign的支持功能
      @EnableFeignClients
      //如果后面需要接入到hystrix-board监控，这边一定要添加熔断器注解
      @EnableCircuitBreaker
      public class SpringCloudAppApiFeignApplication {
      
          public static void main(String[] args) {
              SpringApplication.run(SpringCloudAppApiFeignApplication.class, args);
          }
      
      }
    ```
4. 需要配合微服务spring-cloud-provider-user集群部署，然后多次访问本服务，看是否能实现特定策略的负载均衡，对于熔断器测试，只需shutdown一个user服务，然后在请求，会执行快速失败，直接返回fallback结果，而不是等待响应超时，这很好的控制了容器的线程阻塞
5. 注意：如果后面需要将本feign服务接入hystrix-board监控，则需要额外引入hystrix依赖，并且在启动入口类上添加@EnableCircuitBreaker注解

### 三、代码分析
1. 项目中的UserRest接口就是Feign的客户端接口，需要注意的是:
    - 接口中的方法中参数前面的请求参数注解中的name值不能省略，eg： `@RequestParam("id")Integer id` 不能写成 `@RequestParam Integer id`；
    - 如果微服务调用带有请求头header，可以使用@RequestHeader注解；
    - 如果开启了feign的熔断功能，同时，想要打印fallback熔断异常，则可以使用@FeignClient的fallbackFactory属性，定义一个XXXFallbackFactory实现FallbackFactory接口，重写create方法
2. UserFallbackRest实现了UserRest接口，重写其所有方法，分别表示对应的降级方法，该类通过@FeignClient注解的fallback属性进行配置
3. UserFallbackFactory实现了FallbackFactory<T>接口，重写create方法，可以拦截并打印熔断异常信息，该类可通过@FeignClient注解的fallbackFactory属性进行配置
