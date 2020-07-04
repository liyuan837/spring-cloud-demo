 # 说明
 
 ### 本项目示例：gateway网关
 > 分别路由至spring-cloud-app-api和spring-cloud-provider-user两个服务消费者
 >
 > 并进行简单的鉴权操作

 * 项目介绍
   1. Spring Cloud Gateway是Spring Cloud官方推出的第二代网关框架，取代Zuul网关
   2. 网关作为流量的，在微服务系统中有着非常作用，网关常见的功能有路由转发、权限校验、限流控制等作用
   3. 网关作为一个系统的流量的入口，有着举足轻重的作用，通常的作用如下：
      - 协议转换，路由转发
      - 流量聚合，对流量进行监控，日志输出
      - 作为整个系统的前端工程，对流量进行控制，有限流的作用
      - 作为系统的前端边界，外部流量只能通过网关才能访问系统
      - 可以在网关层做权限的判断
      - 可以在网关层做缓存   
       
 * 项目运行
   1. 引入相关依赖，注意：spring-cloud-starter-gateway 和 spring-boot-starter-web同时引入会造成冲突，所以千万不要引入web依赖
        ```xml
                  <dependency>
                      <groupId>org.springframework.cloud</groupId>
                      <artifactId>spring-cloud-starter-gateway</artifactId>
                  </dependency>
        ```
   2. 配置路由转发规则：
        ```yaml
          spring:
            cloud:
              # gateway模块的路由转发
              gateway:
                routes:
                  - id: app-api
                    # lb代表从注册中心获取服务
                    uri: lb://spring-cloud-app-api
                    predicates:
                      # 转发该路径
                      - Path=/app-api/**
                  - id:  provider-user
                    uri: lb://spring-cloud-provider-user
                    predicates:
                      - Path=/user-api/**
        ```