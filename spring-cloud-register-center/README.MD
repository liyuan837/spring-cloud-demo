# 说明

### 本项目示例：服务注册中心
Eureka提供的服务端，这里我们搭建的是一个双节点的服务注册中心集群

* 单注册中心运行方式（推荐使用）：
  注释掉application.properties.yml中的：
  ```javascript
  spring:
      profiles:
        active: server2
  ```
      
* 双集群注册中心运行方式：
  1.首先运行package对项目进行打包，会在target目录下生成jar包
  
  2.再运行下面命令启动第一个注册中心：通过“http://localhost:1111/”可访问
   java -jar spring-cloud-center-0.0.1-SNAPSHOT.jar --spring.profiles.active=server1
 
  3.然后运行下面命令启动第二个注册中心：通过“http://localhost:1112/”可访问
   java -jar spring-cloud-center-0.0.1-SNAPSHOT.jar --spring.profiles.active=server2
   
* 开启安全认证：
  1. 额外引入SpringSecurity依赖
    ```
       <!--给Eureka Server加上安全的用户认证-->
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-security</artifactId>
       </dependency>
    ```
  2. 在application.properties.yml中spring节点下进行相关配置
    ```javascript
      # 安全认证的配置
      spring:
        security:
          user:
            name: liyuan
            password: liyuan123456
    ```
  3. 服务提供方进行注册时，需要使用：
    http://${name}:${password}@localhost:1111/eureka/
  4. 注意点：当关闭安全认证之后，需在ideal中重新刷新一下右侧的maven依赖，否则springsecurity依赖存在缓存
  
