# 说明

### Reference Documentation
本项目是springcloud搭建的一个全家桶模板项目，各个模块都是springcloud的一个组件：

* spring-cloud-register-center：服务注册中心
* spring-cloud-provider-user：服务提供者
* common-beans：公共模块，包括公共Bean、Exception、req、resp、util等
* spring-cloud-app-api：服务消费者，采用ribbon + hystrix + resttemplate实现
* spring-cloud-app-api-feign：服务消费者，采用feign实现
* spring-cloud-zuul-proxy：zuul网关
  
