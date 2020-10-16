# 说明

### 本项目示例：服务消费者——api[使用zookeeper作为注册中心]
SpringBoot项目，服务消费者，消费zookeeper-provider-user提供的服务
  
* 项目说明
  - 项目使用ribbon和Hystrix分别实现服务调用的客户端负载均衡和熔断功能
  - 由于这里项目直接之间的调用是通过RestTemplate实现的，所以将负载均衡加注到RestTemplate上
  - ribbon默认采用的是RoundRobinRule——轮训策略，可以进行手动调整策略
    - 配置文件方式：
        ```yaml
          #配置文件方式指定负载均衡策略
          SPRING-CLOUD-PROVIDER-USER:
            ribbon:
              NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
        ```
    - javaBean方式：
        ```java
              //更换客户端负载均衡策略，也可以在配置文件中指定服务的负载均衡策略
              @Bean
              public IRule iRule(){
                  return new RandomRule();
              }
        ```
  - 较底层的服务如果出现故障，会导致连锁故障。当对特定的服务的调用的不可用达到一个阀值（Hystrix是5秒20次）断路器将会被打开,断路打开后，可用避免连锁故障，fallback方法可以直接返回一个固定值
  - Hystrix熔断器配置主要通过@HystrixCommand注解实现，通过设置fallback方法来进行熔断降低
    
