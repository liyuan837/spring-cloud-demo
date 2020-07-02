 # 说明
 
 ### 本项目示例：zuul网关
 > 分别路由至spring-cloud-app-api和spring-cloud-provider-user两个服务消费者
 >
 > 并进行简单的鉴权操作

 * 项目介绍
   1.  在Spring Cloud微服务系统中，一种常见的负载均衡方式是，客户端的请求首先经过负载均衡（Ngnix），再到达服务网关（zuul集群），然后再到具体的服务，服务统一注册到高可用的服务注册中心集群，服务的所有的配置文件由配置服务管理，配置服务的配置文件放在git仓库，方便开发人员随时改配置
   2. zuul介绍
       - Zuul的主要功能是路由转发和过滤器
       - zuul默认和Ribbon结合实现了负载均衡的功能
       
 * 项目运行
   1. 主要依赖：
       ```xml
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
            </dependency>
       ```
       
   2. 配置文件中包括application.yml，为每个微服务配置路由规则，并且暴露/routes监控端点
        ```
          server:
            port: 8080
            servlet:
              context-path: /zuul
          
          spring:
            application:
              name: spring-cloud-zuul-proxy
          
          eureka:
            client:
              register-with-eureka: true
              fetch-registry: true
              service-url:
                defaultZone: http://localhost:1111/eureka/
          
          #SpringBoot2.1之后，项目启动时，控制台不自动打印api了，需要做如下配置：更改 org.springframework.web 包的日志级别
          logging:
            level:
              org.springframework.web: TRACE
          
          # 默认路由规则
          zuul:
            routes:
              # 给服务配置路由
              spring-cloud-provider-user:
                path: /user-api/**
              spring-cloud-app-api:
                path: /app-api/**
            # 为网关路由配置前缀
            prefix: /proxy
            # 配置过滤敏感的请求头信息，设置为空就不会过滤
            sensitive-headers: Cookie,Set-Cookie,Authorization
            # 设置为true重定向是会添加host请求头
            add-host-header: true
            # 禁用过滤器
            PreLogFilter:
              pre:
                disable: true
          # 查看路由信息
          management:
            endpoints:
              web:
                exposure:
                  include: 'routes'
        ```
        
   3. 启动入口添加相应注解：
      ```java
            @EnableZuulProxy
            @EnableDiscoveryClient
            @SpringBootApplication
            public class SpringCloudZuulProxyApplication {
            
                public static void main(String[] args) {
                    SpringApplication.run(SpringCloudZuulProxyApplication.class, args);
                }
            
            }
    
        ```
