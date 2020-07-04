# 说明

### Turbine断路器聚合监控(Hystrix Turbine)
本项目是zipkin链路追踪项目，服务追踪组件zipkin，Spring Cloud Sleuth集成了zipkin组件
  
  
* 项目运行
  1. 在spring Cloud为F版本的时候，已经不需要自己构建Zipkin Server了，只需要下载jar即可，下载地址：
     - https://dl.bintray.com/openzipkin/maven/io/zipkin/java/zipkin-server/
  2. 执行启动项目：java -jar zipkin-server-2.10.1-exec.jar
  3. 要使得zipkin-server能够收集微服务链路信息，需要在被监控微服务项目中做如下配置[这里仅对spring-cloud-provider-user和spring-cloud-app-api两个微服务进行配置]：
    - 添加依赖：
        ```xml
                        <!--如果后续需要接入zipkin链路追踪，则需引入-->
                        <dependency>
                            <groupId>org.springframework.cloud</groupId>
                            <artifactId>spring-cloud-starter-sleuth</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.cloud</groupId>
                            <artifactId>spring-cloud-sleuth-zipkin</artifactId>
                        </dependency>
        ```
    - 在其配置文件application.yml指定zipkin server的地址，头通过配置“spring.zipkin.base-url”指定
        ```yaml
          spring:
            # 如果后面引入zipkin链路追踪，则需要
            zipkin:
              base-url: http://localhost:9411
              enabled: true
        ```   
  4. 访问浏览器：http://localhost:9411，可以看到微服务之间的依赖关系以及调用链路