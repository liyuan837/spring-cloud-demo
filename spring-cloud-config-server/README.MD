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
        ```yaml
           spring:
             application:
               # 在注册中心的项目名
               name: spring-cloud-config-server
             cloud:
               config:
                 server:
                   # 配置存储信息的git仓库
                   git:
                     # 配置git仓库地址
                     uri: https://github.com/liyuan837/spring-cloud-demo/
                     # 指定搜索路径，如果有多个路径则使用,分隔
                     search-paths: config-repo
                     # 对于使用git,svn做为后端配置，从远程库获取配置文件，需要存储到本地文件
                     basedir: config-repo/config-repo
                     # 如果是公开仓库，可以不配置账号密码
                     username:
                     password:
                     # 配置中心通过git从远程git库，有时本地的拷贝被污染，这时配置中心无法从远程库更新本地配置,设置force-pull=true，则强制从远程库中更新本地库
                     force-pull: true
                 label: master
        ```
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
    
    4. 访问方式：__
       - http://localhost:9001/gateway/prod/master
          - http://localhost:9001/{配置文件名前缀}/{配置文件版本}/git分支名
          - 比如：gateway-dev.properties的配置文件前缀就市gateway
       - http://localhost:9001/gateway-test.properties
          - http://localhost:9001/{配置文件名称}
    5. 注意：
        当我们更改了远程git仓库配置文件信息时，注册中心的数据会自动加载，所以当我们使用上面的访问方式访问时，能实时拿到最新配置数据
