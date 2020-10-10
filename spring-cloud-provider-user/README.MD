# 说明

## 本项目示例：服务提供者——用户模块
> 1. SpringBoot项目，服务提供者，这里我们搭建的是一个双节点的服务提供者集群
>
> 2. 一般服务提供者是按照业务模块进行最小化分的，从controller -> service -> dao层进行构建，最终向上层api服务提供最小模块功能支撑

### 一、单节点运行方式（推荐使用）：
1. 注释掉application.yml中的：
      ```javascript
      spring:
          profiles:
            active: server2
      ```
      
### 二、双集群服务运行方式：
1. 首先运行package对项目进行打包，会在target目录下生成jar包
  
2. 再运行下面命令启动第一个服务提供者：通过“http://localhost:7001/”可访问
   - java -jar spring-cloud-provider-user-0.0.1-SNAPSHOT.jar --spring.profiles.active=server1
 
3. 然后运行下面命令启动第二个注册中心：通过“http://localhost:7002/”可访问
   - java -jar spring-cloud-provider-user-0.0.1-SNAPSHOT.jar --spring.profiles.active=server2
  
4. 多集群运行，是为了方便后面spring-cloud-app-api中进行客户端负载均衡策略的测试

### 三、springboot自带的actuator监控
  > actuator 提供了很多api（称为：节点），默认只开放了 health、info两个节点
  >
  > GET /autoconfig 查看自动配置的使用情况 true 
  > GET /configprops 查看配置属性，包括默认配置 true 
  > GET /beans 查看bean及其关系列表 true 
  > GET /dump 打印线程栈 true 
  > GET /env 查看所有环境变量 true 
  > GET /env/{name} 查看具体变量值 true 
  > GET /health 查看应用健康指标 false 
  > GET /info 查看应用信息 false 
  > GET /mappings 查看所有url映射 true 
  > GET /metrics 查看应用基本指标 true 
  > GET /metrics/{name} 查看具体指标 true 
  > POST /shutdown 关闭应用 true 
  > GET /trace 查看基本追踪信息 true 
  >
  > 更多关于actuator的内容请参考：https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#production-ready-endpoints
1. 需引入actuator依赖
    ```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    ```
2. 至于启用方式，springboot2.0改动比较大，需在配置文件中添加
    ```yaml
        management:
          endpoints:
            web:
              # Web端点的基本路径,默认为/actuator
              base-path: /actuator
              # 默认只开放health,info端点  用*可以包含全部端点
              exposure:
                include: "*"
    ```
  
### 四、注册中心查看服务
1. Eureka服务器提供了一个端点（/eureka/apps/[APPID]）可以查看所注册的服务详细信息
    - http://localhost:1111/eureka/apps/SPRING-CLOUD-PROVIDER-USER
