 # 说明
 
 ### 本项目示例：消息总线
 > Spring Cloud Bus 将分布式的节点用轻量的消息代理连接起来。它可以用于广播配置文件的更改或者服务之间的通讯，也可以用于监控
 >
 > 需安装rabbitmq，并启动在5672端口上
 * 项目运行
    1. 配置中心主要依赖：
       ```xml
          <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-config-client</artifactId>
          </dependency>
          <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-bus-amqp</artifactId>
          </dependency>
       ```
       
    2. 配置文件中包括bootstrap.yml、application.yml
        - bootstrap.yml优先执行：
            ```
               spring:
                 # rabbitmq相关配置
                 rabbitmq:
                   host: localhost
                   port: 5672
                   username: guest
                   password: guest
                 cloud:
                   config:
                     # 请求的具体分支，该demo使用master
                     label: master
                     # config server的地址
                     uri: http://localhost:9001
                     # 远程的具体配置文件，可以写多个，通过逗号隔开，
                     # 该demo使用 https://github.com/liyuan837/springcloud_demo/config-repo/gateway-test.properties，name配置文件前缀
                     name: gateway
                     # 使用哪个环境的配置，如dev、test、stage、prod
                     profile: test
               
                     bus:
                       enabled: true
                       trace:
                         enabled: true
            ```
        - application.yml正常配置：
    3. 配置数据注入获取：
      ```java
        @RefreshScope
        @RestController
        @RequestMapping("/config")
        @Api(value = "/config", description = "")
        public class ConfigController{
        
            @Value("${from}")
            private String from;
        
            @ApiOperation(value = "获取from参数配置",notes = "获取from参数配置",httpMethod = "GET")
            @GetMapping
            public String getFrom(){
                return from;
            }
        }
        ```
    4. 注意：