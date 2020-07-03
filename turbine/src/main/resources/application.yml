server:
  port: 2002

spring:
  application:
    # 在注册中心的项目名
    name: turbine

eureka:
  instance:
    # eureka客户端定义服务续约时间
    lease-renewal-interval-in-seconds: 30
    instance-id: ${spring.application.name}:${random.int}
  client:
    register-with-eureka: true
    fetch-registry: true
    serviceUrl:
      #注册中心地址,default-zone: http://${name}:${password}@localhost:1111/eureka/
      defaultZone: http://localhost:1111/eureka/

#SpringBoot2.1之后，项目启动时，控制台不自动打印api了，需要做如下配置：更改 org.springframework.web 包的日志级别
logging:
  level:
    org.springframework.web: TRACE

#actuator监控端点
management:
  endpoints:
    web:
      #Web端点的基本路径,默认为/actuator
      base-path: /actuator
      #默认值访问health,info端点  用*可以包含全部端点
      exposure:
        include: "*"
      cors:
        allowed-origins: "*"
        allowed-methods: "*"

turbine:
  # 监控的服务列表
  app-config: spring-cloud-app-api,spring-cloud-app-api-feign
  aggregator:
    cluster-config: default
  cluster-name-expression: new String("default")
  instanceUrlSuffix: api/actuator/hystrix.stream
