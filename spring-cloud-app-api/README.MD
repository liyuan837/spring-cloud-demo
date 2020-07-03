# 说明

### 本项目示例：服务消费者——api
SpringBoot项目，服务消费者，消费spring-cloud-provider-user提供的服务
  
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
     
* 项目启动
  1. 引入依赖
    ```pom
            <!--客户端负载均衡器-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
            </dependency>
    
            <!-- 服务消费者中加入断路器 hystrix监控-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
            </dependency>
    ```
  2. 启动类添加相关注解
    ```java
        @SpringBootApplication
        //声明此服务是服务消费者
        @EnableDiscoveryClient
        //开启断路器功能
        @EnableCircuitBreaker
        public class SpringCloudAppApiApplication {
        
            //实现客户端负载均衡
            @LoadBalanced
            @Bean
            RestTemplate restTemplate() {
                return new RestTemplate();
            }
        
            //更换客户端负载均衡策略，也可以在配置文件中指定服务的负载均衡策略
            @Bean
            public IRule iRule(){
                return new RandomRule();
            }
        
        
            public static void main(String[] args) {
                SpringApplication.run(SpringCloudAppApiApplication.class, args);
            }
        
        }
    ```
  3. 需要配合微服务spring-cloud-provider-user集群部署，然后多次访问本服务，看是否能实现特定策略的负载均衡，对于熔断器测试，只需shutdown一个user服务，然后在请求，会执行快速失败，直接返回fallback结果，而不是等待响应超时，这很好的控制了容器的线程阻塞
  
  
* ribbon源码学习
  1. 什么是ribbon?
    - Ribbon是`Netflix`公司开源的一个负载均衡的项目，是一个客户端负载均衡器，运行在客户端上，这边所谓的客户端，是针对于服务提供者来说，服务调用者就是客户端
  2. RestTemplate和Ribbon相结合
    - 在feign中默认使用了ribbon，在zuul中也是使用ribbon做负载均衡，在springcloud中，做的最多的就是将RestTemplate和ribbon结合：
        ```java
              @LoadBalanced
              @Bean
              RestTemplate restTemplate() {
                  return new RestTemplate();
              }
        ```
    - 消费另外一个服务的接口，使用如下方式：
        ```java
              @Autowired
              private RestTemplate restTemplate;
      
              protected String dealGetServer(String url,Map<String,Object> paramVariables) {
          
                  StringBuilder sb = new StringBuilder(64);
          
                  // 封装必要参数
                  sb.append(url).append("?timestamp=").append(System.currentTimeMillis());
          
                  if(MapUtils.isNotEmpty(paramVariables)){
                      paramVariables.entrySet().stream().forEach(entry -> sb.append("&").append(entry.getKey()).append("=").append(entry.getValue()));
                  }
          
                  return restTemplate.getForObject(sb.toString(), String.class);
              }
        ```
  3. 深入理解ribbon
    - LoadBalanceClient
       - 在Riibon中一个非常重要的组件为LoadBalancerClient，它作为负载均衡的一个客户端
       - 存放在spring-cloud-commons包下，LoadBalancerClient是一个接口，它继承ServiceInstanceChooser，它的实现类是RibbonLoadBalancerClient
       - 三者关系如下：
            ![LoadBalancerClient.png](img/LoadBalancerClient.png)
       - 在LoadBalancerClient接口中定义了三个方法，其中execute()是执行请求，reconstructURI()用来重构url：
            ```java
                public interface LoadBalancerClient extends ServiceInstanceChooser {
                    <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException;
                
                    <T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request) throws IOException;
                
                    URI reconstructURI(ServiceInstance instance, URI original);
                }
            ```
       - ServiceInstanceChooser接口，存放在spring-cloud-commons包下，就一个方法用来根据serviceId选取ServiceInstance：
            ```java
                 public interface ServiceInstanceChooser {
                     ServiceInstance choose(String serviceId);
                 }
            ```
       - LoadBalancerClient的一个实现类是RibbonLoadBalancerClient，是ribbon包org.springframework.cloud.netflix.ribbon下的一个实现类
       - RibbonLoadBalancerClient是非常重要的一个类，最终的负载均衡的请求处理，由它来执行，其部分源码如下：
            > 其中choose()方法是选择具体服务实例的一个方法。该方法通过getServer()方法去获取实例，经过源码跟踪，最终交给了ILoadBalancer类去选择服务实例
                                                                        
            ```java
                 public class RibbonLoadBalancerClient implements LoadBalancerClient {
                     //省略部分...
         
                     public ServiceInstance choose(String serviceId) {
                         return this.choose(serviceId, (Object)null);
                     }
                 
                     public ServiceInstance choose(String serviceId, Object hint) {
                         Server server = this.getServer(this.getLoadBalancer(serviceId), hint);
                         return server == null ? null : new RibbonLoadBalancerClient.RibbonServer(serviceId, server, this.isSecure(server, serviceId), this.serverIntrospector(serviceId).getMetadata(server));
                     }
                 
                     public <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException {
                         return this.execute(serviceId, (LoadBalancerRequest)request, (Object)null);
                     }
                 
                     public <T> T execute(String serviceId, LoadBalancerRequest<T> request, Object hint) throws IOException {
                         ILoadBalancer loadBalancer = this.getLoadBalancer(serviceId);
                         Server server = this.getServer(loadBalancer, hint);
                         if (server == null) {
                             throw new IllegalStateException("No instances available for " + serviceId);
                         } else {
                             RibbonLoadBalancerClient.RibbonServer ribbonServer = new RibbonLoadBalancerClient.RibbonServer(serviceId, server, this.isSecure(server, serviceId), this.serverIntrospector(serviceId).getMetadata(server));
                             return this.execute(serviceId, (ServiceInstance)ribbonServer, (LoadBalancerRequest)request);
                         }
                     }
                 
                     public <T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request) throws IOException {
                         Server server = null;
                         if (serviceInstance instanceof RibbonLoadBalancerClient.RibbonServer) {
                             server = ((RibbonLoadBalancerClient.RibbonServer)serviceInstance).getServer();
                         }
                 
                         if (server == null) {
                             throw new IllegalStateException("No instances available for " + serviceId);
                         } else {
                             RibbonLoadBalancerContext context = this.clientFactory.getLoadBalancerContext(serviceId);
                             RibbonStatsRecorder statsRecorder = new RibbonStatsRecorder(context, server);
                 
                             try {
                                 T returnVal = request.apply(serviceInstance);
                                 statsRecorder.recordStats(returnVal);
                                 return returnVal;
                             } catch (IOException var8) {
                                 statsRecorder.recordStats(var8);
                                 throw var8;
                             } catch (Exception var9) {
                                 statsRecorder.recordStats(var9);
                                 ReflectionUtils.rethrowRuntimeException(var9);
                                 return null;
                             }
                         }
                     }
                 
                     private ServerIntrospector serverIntrospector(String serviceId) {
                         ServerIntrospector serverIntrospector = (ServerIntrospector)this.clientFactory.getInstance(serviceId, ServerIntrospector.class);
                         if (serverIntrospector == null) {
                             serverIntrospector = new DefaultServerIntrospector();
                         }
                 
                         return (ServerIntrospector)serverIntrospector;
                     }
                 
                     private boolean isSecure(Server server, String serviceId) {
                         IClientConfig config = this.clientFactory.getClientConfig(serviceId);
                         ServerIntrospector serverIntrospector = this.serverIntrospector(serviceId);
                         return RibbonUtils.isSecure(config, serverIntrospector, server);
                     }
                 
                     protected Server getServer(String serviceId) {
                         return this.getServer(this.getLoadBalancer(serviceId), (Object)null);
                     }
                 
                     protected Server getServer(ILoadBalancer loadBalancer) {
                         return this.getServer(loadBalancer, (Object)null);
                     }
                 
                     protected Server getServer(ILoadBalancer loadBalancer, Object hint) {
                         return loadBalancer == null ? null : loadBalancer.chooseServer(hint != null ? hint : "default");
                     }
                 
                     protected ILoadBalancer getLoadBalancer(String serviceId) {
                         return this.clientFactory.getLoadBalancer(serviceId);
                     }
                     //省略部分...
                 
                 }
            ```
       - ILoadBalancer：在ribbon-loadbalancer的jar包下，它是定义了实现软件负载均衡的一个接口，它需要一组可供选择的服务注册列表信息，以及根据特定方法去选择服务
       - ILoadBalancer的源码如下：
            ```java
                 public interface ILoadBalancer {
                     // addServers()方法是添加一个Server集合
                     void addServers(List<Server> var1);
         
                     // chooseServer()方法是根据key去获取Server
                     Server chooseServer(Object var1);
                     
                     // markServerDown()方法用来标记某个服务下线
                     void markServerDown(Server var1);
                 
                     /** @deprecated */
                     @Deprecated
                     List<Server> getServerList(boolean var1);
                     
                     // getReachableServers()获取可用的Server集合
                     List<Server> getReachableServers();
                 
                     // getAllServers()获取所有的Server集合
                     List<Server> getAllServers();
                 }
            ```
    - DynamicServerListLoadBalancer
       - DynamicServerListLoadBalancer继承BaseLoadBalancer
       - 关系如下：
            ![LoadBalancerClient.png](img/DynamicServerListLoadBalancer.png)
  4. RestTemplate是如何和Ribbon结合的
    
  5. 总结
  
    
    
    
    
    
    
    
    
    
    
    
    
    
