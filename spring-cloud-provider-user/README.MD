# 说明

### 本项目示例：服务提供者——用户模块
SpringBoot项目，服务提供者，这里我们搭建的是一个双节点的服务提供者集群

`一般服务提供者是按照业务模块进行最小化分的，从controller -> service -> dao层进行构建，最终向上层api服务提供最小模块功能支撑`

* 单注册中心运行方式（推荐使用）：
  注释掉application.yml中的：
  ```javascript
  spring:
      profiles:
        active: server2
  ```
      
* 双集群服务运行方式：
  1.首先运行package对项目进行打包，会在target目录下生成jar包
  
  2.再运行下面命令启动第一个服务提供者：通过“http://localhost:7001/”可访问
 java -jar spring-cloud-provider-user-0.0.1-SNAPSHOT.jar --spring.profiles.active=server2
 
  3.然后运行下面命令启动第二个注册中心：通过“http://localhost:7002/”可访问
  
  4.多集群运行，是为了方便后面spring-cloud-app-api中进行客户端负载均衡策略的测试
