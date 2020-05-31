 # 说明
 
 ### 本项目示例：配置中心-服务端
 
 * 配置中心主要依赖：
   ```xml
      <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
      </dependency>
   ```
       
 * 配置文件中配置git相关信息
    主要是配置git相关信息，以及将本服务作为eureka客户端注册到注册中心
    
 * 访问方式：
       http://localhost:9001/gateway/prod/master
       http://localhost:9001/{配置文件名前缀}/{配置文件版本}/git分支名
   
       或
       
       http://localhost:9001/gateway-test.properties
       http://localhost:9001/{配置文件名称}
 * 注意：
    当我们更改了远程git仓库配置文件信息时，注册中心的数据会自动加载，所以当我们使用上面的访问方式访问时，能实时拿到最新配置数据