# 说明

### Turbine断路器聚合监控(Hystrix Turbine)
本项目是Turbine实现断路器的聚合监控，本项目首先需要引入hystrix-board，以便于图形化监控turbine.stream

* Hystrix Turbine简介
  - 之前用到的Hystrix Dashboard的数据只能监控一个服务的断路器状态，对于一个集群化部署的服务，并没有什么多大的价值
  - 要想看这个系统的Hystrix Dashboard数据就需要用到Hystrix Turbine
  - Hystrix Turbine将每个服务Hystrix Dashboard数据进行了整合
  
* 项目运行
  1. 引入turbine相关依赖
    ```xml
               <dependency>
                   <groupId>org.springframework.cloud</groupId>
                   <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
               </dependency>
               <dependency>
                   <groupId>org.springframework.cloud</groupId>
                   <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
               </dependency>
       
               <dependency>
                   <groupId>org.springframework.cloud</groupId>
                   <artifactId>spring-cloud-starter-netflix-turbine</artifactId>
               </dependency>
    ```
  2. 配置文件：
    ```xml
      turbine:
        # 监控的服务列表
        app-config: spring-cloud-app-api,spring-cloud-app-api-feign
        aggregator:
          cluster-config: default
        cluster-name-expression: new String("default")
        instanceUrlSuffix: api/actuator/hystrix.stream
    ```
  3. 启动入口类上添加@EnableTurbine注解
    ```java
        @SpringBootApplication
        @EnableDiscoveryClient
        @EnableHystrix
        @EnableHystrixDashboard
        @EnableCircuitBreaker
        @EnableTurbine
        public class TurbineApplication {
        
            public static void main(String[] args) {
                SpringApplication.run(TurbineApplication.class, args);
            }
        
        }
    ```
  4. 访问:http://localhost:2001/hystrix打开监控面板
    - 输入全局监控url：http://localhost:2002/turbine.stream
  5. 注意
    - springboot2.0之后，turbine默认监控微服务实例 的路径是actuator/hystrix.stream
    - 而我们这边徐监控的微服务spring-cloud-app-api,spring-cloud-app-api-feign暴露出来的端口是：api/actuator/hystrix.stream，所以这边需要重新配置turbine.instanceUrlSuffix值
