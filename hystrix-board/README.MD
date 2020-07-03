# 说明

### 本项目示例：hystrix-board
SpringBoot项目，hystrix仪表盘监控，用来监控同一个注册中心注册的hystrix的服务
  
* hystrix board简介
  1. 在微服务架构中为例保证程序的可用性，防止程序出错导致网络阻塞，出现了断路器模型
  2. 断路器的状况反应了一个程序的可用性和健壮性，它是一个重要指标
  3. Hystrix Dashboard是作为断路器状态的一个组件，提供了数据监控和友好的图形化界面
  
* 项目运行
  1. 引入actuator和hystrix-board依赖
    ```xml
              <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-starter-actuator</artifactId>
              </dependency>
              <dependency>
                  <groupId>org.springframework.cloud</groupId>
                  <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
              </dependency>
    ```
  2. 启动入口类加入注解
    ```java
      @SpringBootApplication
      @EnableDiscoveryClient
      @EnableHystrixDashboard
      public class HystrixBoardApplication {
      
          public static void main(String[] args) {
              SpringApplication.run(HystrixBoardApplication.class, args);
          }
      
      }
    ```
  3. 访问仪表盘主界面url:http://localhost:2001/hystrix
  
  4. 然后又输入你要查看的微服务暴露出来的/actuator/hystrix.stream监控端口，
    - 比如：http://localhost:8082/api/actuator/hystrix.stream
    - 点击会进入图形化的监控界面，然后手动调用下微服务的接口，这个监控界面会实时刷新
   
  5. 注意
    - 被监控微服务一定要引入hystrix熔断器和actuator监控端点，比如http://localhost:7001/user/actuator/hystrix.stream 就无法实现监控
    - 对于feign服务的监控，需要feign服务额外引入hystrix依赖，启动入口类上添加@EnableCircuitBreaker注解
