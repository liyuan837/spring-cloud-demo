 # 说明
 
 ### 本项目示例：配置中心-服务端
 
 * 项目说明
    - 在分布式系统中，由于服务数量巨多，为了方便服务配置文件统一管理，实时更新，所以需要分布式配置中心组件
    - Spring Cloud中，有分布式配置中心组件spring cloud config ，它支持配置服务放在配置服务的内存中（即本地），也支持放在远程Git仓库中
    - 在spring cloud config 组件中，分两个角色，一是config server，二是config client
 
 
 * 项目运行
    1. 配置中心主要依赖config：
       ```xml
          <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-config-server</artifactId>
          </dependency>
       ```
    2. 配置文件中配置git相关信息
        主要是配置git相关信息，以及将本服务作为eureka客户端注册到注册中心
    
    3. 启动入口类添加config的相关注解
        ```java
           @SpringBootApplication
           //声明配置中心服务端
           @EnableConfigServer
           @EnableDiscoveryClient
           public class SpringCloudConfigServerApplication {
           
               public static void main(String[] args) {
                   SpringApplication.run(SpringCloudConfigServerApplication.class, args);
               }
           
           }
        ```
    
    4. 访问方式：
       - http://localhost:9001/gateway/prod/master
          - http://localhost:9001/{配置文件名前缀}/{配置文件版本}/git分支名
          - 比如：gateway-dev.properties的配置文件前缀就市gateway
       - http://localhost:9001/gateway-test.properties
          - http://localhost:9001/{配置文件名称}
    5. 注意：
        当我们更改了远程git仓库配置文件信息时，注册中心的数据会自动加载，所以当我们使用上面的访问方式访问时，能实时拿到最新配置数据
