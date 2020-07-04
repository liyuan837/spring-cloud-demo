 # 说明
 
 ### 本项目示例：配置中心-客户端
 
 * 项目运行
    1. 配置中心主要依赖：
       ```xml
          <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-config-client</artifactId>
          </dependency>
       ```
       
    2. 配置文件中包括bootstrap.yml、application.yml
        - bootstrap.yml优先执行：
            ```
               spring:
                  cloud:
                    config:
                      # 请求的具体分支，该demo使用master
                      label: master
                      # config server的地址
                      uri: http://localhost:9001
                      # 远程的具体配置文件，可以写多个，通过逗号隔开，
                      # 该demo使用 https://github.com/liyuan837/spring-cloud-demo/config-repo/gateway-test.properties，name配置文件前缀
                      name: gateway
                      # 使用哪个环境的配置，如dev、test、stage、prod
                      profile: test
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
    4. 注意：被@RefreshScope修饰的Bean是延迟加载的，只有在第一次访问时才会被初始化，刷新Bean也是同理，下次访问会创建一个新的对象。
       所以当更改了远程git仓库配置文件时，配置中心服务端会实时相应更新，但是配置中心客户端不会；这边需要在客户端手动调用POST http://localhost:9002/actuator/refresh 进行刷新，注意该操作一定要用post，实际生成中可以结合Spring Cloud Bus或其他组件进行自动刷新，然后再次访问，即可获取最新的配置数据
