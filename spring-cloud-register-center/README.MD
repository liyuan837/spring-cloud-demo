
# 说明

## 本项目示例：Eureka服务注册中心
Eureka提供的服务端，这里我们搭建的是一个双节点的服务注册中心集群

### 一、单注册中心运行方式（推荐使用）
1. 注释掉application.properties.yml中的：
	```
		spring:
          profiles:
            active: server2
	```

### 二、双集群注册中心运行方式
1.首先运行package对项目进行打包，会在target目录下生成eureka-server.jar包

2.再运行下面命令启动第一个注册中心：通过“http://localhost:1111/”可访问
   - java -jar eureka-server.jar --spring.profiles.active=server1

3.然后运行下面命令启动第二个注册中心：通过“http://localhost:1112/”可访问
   - java -jar eureka-server.jar --spring.profiles.active=server2

### 三、开启安全认证：
1. 额外引入SpringSecurity依赖
    ```
       <!--给Eureka Server加上安全的用户认证-->
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-security</artifactId>
       </dependency>
    ```

  2. 在application.properties.yml中spring节点下进行相关配置

     ```javascript
       # 安全认证的配置
       spring:
         security:
           user:
             name: ******
             password: ************
     ```

  3. 服务提供方进行注册时，需要使用：
     - http://${name}:${password}@localhost:1111/eureka/

  4. 注意点：当关闭安全认证之后，需在ideal中重新刷新一下右侧的maven依赖，否则springsecurity依赖存在缓存

### 四、其他一些注册中心(https://blog.csdn.net/qq_19467623/article/details/96444902)
  1. consul
  2. nacos
  3. zookeeper
 

### 五、Eureka源码学习
  1. *eureka相关概念
     
        - **Register：服务注册**
          - 当Eureka客户端向Eureka Server注册时，它提供自身的元数据，比如IP地址、端口，运行状况指示符URL，主页等
        - **Renew：服务续约（客户端配置）**
          - Eureka客户会每隔30秒发送一次心跳来续约。 通过续约来告知Eureka Server该Eureka客户仍然存在，没有出现问题（lease-renewal-interval-in-seconds）
          - 正常情况下，如果Eureka Server在90秒没有收到Eureka客户的续约，它会将实例从其注册表中删除。建议不要更改续约间隔（lease-expiration-duration-in-seconds）
        - **Fetch Registries：获取注册列表信息** 
          - Eureka客户端从服务器获取注册表信息，并将其缓存在本地（registry-fetch-interval-seconds）
          - 客户端会使用该信息查找其他服务，从而进行远程调用。该注册列表信息定期（每30秒钟）更新一次
          - 每次返回注册列表信息可能与Eureka客户端的缓存信息不同， Eureka客户端自动处理。
          - 如果由于某种原因导致注册列表信息不能及时匹配，Eureka客户端则会重新获取整个注册表信息。
          - Eureka服务器缓存注册列表信息，整个注册表以及每个应用程序的信息进行了压缩，压缩内容和没有压缩的内容完全相同
          - Eureka客户端和Eureka 服务器可以使用JSON / XML格式进行通讯。在默认的情况下Eureka客户端使用压缩JSON格式来获取注册列表的信息
        - **Cancel：服务下线**
          - Eureka客户端在程序关闭时向Eureka服务器发送取消请求
          - 发送请求后，该客户端实例信息将从服务器的实例注册表中删除
          - 该下线请求不会自动完成，它需要调用以下内容：DiscoveryManager.getInstance().shutdownComponent()；
        - **Eviction 服务剔除** 
          - 在默认的情况下，当Eureka客户端连续90秒没有向Eureka服务器发送服务续约，即心跳，Eureka服务器会将该服务实例从服务注册列表删除，即服务剔除
        
  2. Eureka高可用架构

        - Eureka开元代码文档提供了如下的Eureka的高级架构图：

          ![img/eureka.png](img/eureka.png)

          - 在该体系中，有两个角色：Eureka Server（服务端）和Eureka Client（客户端），Eureka Client又分为Application Service（服务提供者）和Application Client（服务消费者）
          - 每个区域有一个Eureka集群，并且每个区域至少有一个eureka服务器可以处理区域故障，以防服务器瘫痪
          - Eureka Client向Eureka Server注册，并将自己的一些客户端信息发送给Eureka  Server
          - 然后，Eureka Client通过向Eureka Serve发送心跳（每30秒）来续约服务的
          - 如果客户端持续不能续约，那么，它将在大约90秒内从服务器注册表中删除
          - 注册信息和续订被复制到集群中的Eureka Serve所有节点
          - 来自任何区域的Eureka Client都可以查找注册表信息（每30秒发生一次）
          - Application Client可以远程调用Application Service来消费服务

  3. Register注册服务

        - 服务注册，即Eureka Client向Eureka Server提交自己的服务信息，包括IP地址、端口、service ID等信息【如果Eureka Client没有写service ID，则默认为 ${spring.application.name}】

        - 客户端服务注册流程

          - 在Eureka Client启动的时候，将自身的服务的信息发送到Eureka Server

          - 找到eureka-client-1.9.13.jar包下的com.netflix.discovery.DiscoveryClient类，该类包含了Eureka Client向Eureka Server的相关方法，它实现了EurekaClient接口，EurekaClient接口是一个单例，它继承了LookupService接口，他们之间关系如下：

            ![DiscoveryClient.png](img/DiscoveryClient.png)

          - 在DiscoveryClient类有一个服务注册的方法register()，该方法是通过Http请求向Eureka Client注册，源码如下:

            ```
                boolean register() throws Throwable {
                    logger.info("DiscoveryClient_{}: registering service...", this.appPathIdentifier);
            
                    EurekaHttpResponse httpResponse;
                    try {
                        httpResponse = this.eurekaTransport.registrationClient.register(this.instanceInfo);
                    } catch (Exception var3) {
                        logger.warn("DiscoveryClient_{} - registration failed {}", new Object[]{this.appPathIdentifier, var3.getMessage(), var3});
                        throw var3;
                    }
            
                    if (logger.isInfoEnabled()) {
                        logger.info("DiscoveryClient_{} - registration status: {}", this.appPathIdentifier, httpResponse.getStatusCode());
                    }
            
                    return httpResponse.getStatusCode() == Status.NO_CONTENT.getStatusCode();
                }
            ```

          - 在DiscoveryClient类继续追踪register()方法，它被InstanceInfoReplicator 类的run()方法调用，其中InstanceInfoReplicator实现了Runnable接口，run()方法代码如下：

            ```
             public void run() {
                    boolean var6 = false;
            
                    ScheduledFuture next;
                    label53: {
                        try {
                            var6 = true;
                            this.discoveryClient.refreshInstanceInfo();
                            Long dirtyTimestamp = this.instanceInfo.isDirtyWithTime();
                            if (dirtyTimestamp != null) {
                            	// 我在这里
                                this.discoveryClient.register();
                                this.instanceInfo.unsetIsDirty(dirtyTimestamp);
                                var6 = false;
                            } else {
                                var6 = false;
                            }
                            break label53;
                        } catch (Throwable var7) {
                            logger.warn("There was a problem with the instance info replicator", var7);
                            var6 = false;
                        } finally {
                            if (var6) {
                                ScheduledFuture next = this.scheduler.schedule(this, (long)this.replicationIntervalSeconds, TimeUnit.SECONDS);
                                this.scheduledPeriodicRef.set(next);
                            }
                        }
            
                        next = this.scheduler.schedule(this, (long)this.replicationIntervalSeconds, TimeUnit.SECONDS);
                        this.scheduledPeriodicRef.set(next);
                        return;
                    }
            
                    next = this.scheduler.schedule(this, (long)this.replicationIntervalSeconds, TimeUnit.SECONDS);
                    this.scheduledPeriodicRef.set(next);
                }
            ```

          - 而InstanceInfoReplicator类是在DiscoveryClient初始化过程中使用的，其中有一个initScheduledTasks()方法，该方法主要开启了获取服务注册列表的信息，如果需要向Eureka Server注册，则开启注册，同时开启了定时向Eureka Server服务续约的定时任务，具体代码如下：

            ```
             private void initScheduledTasks() {
                    int renewalIntervalInSecs;
                    int expBackOffBound;
                    //任务调度获取注册列表的代码
                    if (this.clientConfig.shouldFetchRegistry()) {
                        renewalIntervalInSecs = this.clientConfig.getRegistryFetchIntervalSeconds();
                        expBackOffBound = this.clientConfig.getCacheRefreshExecutorExponentialBackOffBound();
                        this.scheduler.schedule(new TimedSupervisorTask("cacheRefresh", this.scheduler, this.cacheRefreshExecutor, renewalIntervalInSecs, TimeUnit.SECONDS, expBackOffBound, new DiscoveryClient.CacheRefreshThread()), (long)renewalIntervalInSecs, TimeUnit.SECONDS);
                    }
            
                    if (this.clientConfig.shouldRegisterWithEureka()) {
                        renewalIntervalInSecs = this.instanceInfo.getLeaseInfo().getRenewalIntervalInSecs();
                        expBackOffBound = this.clientConfig.getHeartbeatExecutorExponentialBackOffBound();
                        logger.info("Starting heartbeat executor: renew interval is: {}", renewalIntervalInSecs);
                        //我在这里
                        this.scheduler.schedule(new TimedSupervisorTask("heartbeat", this.scheduler, this.heartbeatExecutor, renewalIntervalInSecs, TimeUnit.SECONDS, expBackOffBound, new DiscoveryClient.HeartbeatThread()), (long)renewalIntervalInSecs, TimeUnit.SECONDS);
                        this.instanceInfoReplicator = new InstanceInfoReplicator(this, this.instanceInfo, this.clientConfig.getInstanceInfoReplicationIntervalSeconds(), 2);
                        this.statusChangeListener = new StatusChangeListener() {
                            public String getId() {
                                return "statusChangeListener";
                            }
            
                            public void notify(StatusChangeEvent statusChangeEvent) {
                                if (InstanceStatus.DOWN != statusChangeEvent.getStatus() && InstanceStatus.DOWN != statusChangeEvent.getPreviousStatus()) {
                                    DiscoveryClient.logger.info("Saw local status change event {}", statusChangeEvent);
                                } else {
                                    DiscoveryClient.logger.warn("Saw local status change event {}", statusChangeEvent);
                                }
            
                                DiscoveryClient.this.instanceInfoReplicator.onDemandUpdate();
                            }
                        };
                        if (this.clientConfig.shouldOnDemandUpdateStatusChange()) {
                            this.applicationInfoManager.registerStatusChangeListener(this.statusChangeListener);
                        }
            
                        this.instanceInfoReplicator.start(this.clientConfig.getInitialInstanceInfoReplicationIntervalSeconds());
                    } else {
                        logger.info("Not registering with Eureka server per configuration");
                    }
            
                }
            ```

        - 服务端服务注册流程

          - 找到eureka-client-1.9.13.jar包下的com.netflix.eureka.EurekaBootStrap类，BootStrapContext具有最先初始化的权限，源码如下：

            ```
             protected void initEurekaServerContext() throws Exception {
                    EurekaServerConfig eurekaServerConfig = new DefaultEurekaServerConfig();
            
                    // For backward compatibility
                    JsonXStream.getInstance().registerConverter(new V1AwareInstanceInfoConverter(), XStream.PRIORITY_VERY_HIGH);
                    XmlXStream.getInstance().registerConverter(new V1AwareInstanceInfoConverter(), XStream.PRIORITY_VERY_HIGH);
            
                    logger.info("Initializing the eureka client...");
                    logger.info(eurekaServerConfig.getJsonCodecName());
                    ServerCodecs serverCodecs = new DefaultServerCodecs(eurekaServerConfig);
            
                    ApplicationInfoManager applicationInfoManager = null;
            
                    if (eurekaClient == null) {
                        EurekaInstanceConfig instanceConfig = isCloud(ConfigurationManager.getDeploymentContext())
                                ? new CloudInstanceConfig()
                                : new MyDataCenterInstanceConfig();
                        
                        applicationInfoManager = new ApplicationInfoManager(
                                instanceConfig, new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get());
                        
                        EurekaClientConfig eurekaClientConfig = new DefaultEurekaClientConfig();
                        eurekaClient = new DiscoveryClient(applicationInfoManager, eurekaClientConfig);
                    } else {
                        applicationInfoManager = eurekaClient.getApplicationInfoManager();
                    }
            		//关注点开始
                    PeerAwareInstanceRegistry registry;
                    if (isAws(applicationInfoManager.getInfo())) {
                        registry = new AwsInstanceRegistry(
                                eurekaServerConfig,
                                eurekaClient.getEurekaClientConfig(),
                                serverCodecs,
                                eurekaClient
                        );
                        awsBinder = new AwsBinderDelegate(eurekaServerConfig, eurekaClient.getEurekaClientConfig(), registry, applicationInfoManager);
                        awsBinder.start();
                    } else {
                        registry = new PeerAwareInstanceRegistryImpl(
                                eurekaServerConfig,
                                eurekaClient.getEurekaClientConfig(),
                                serverCodecs,
                                eurekaClient
                        );
                    }
            
                    PeerEurekaNodes peerEurekaNodes = getPeerEurekaNodes(
                            registry,
                            eurekaServerConfig,
                            eurekaClient.getEurekaClientConfig(),
                            serverCodecs,
                            applicationInfoManager
                    );
            		//关注点结束
            
                    serverContext = new DefaultEurekaServerContext(
                            eurekaServerConfig,
                            serverCodecs,
                            registry,
                            peerEurekaNodes,
                            applicationInfoManager
                    );
            
                    EurekaServerContextHolder.initialize(serverContext);
            
                    serverContext.initialize();
                    logger.info("Initialized server context");
            
                    // Copy registry from neighboring eureka node
                    int registryCount = registry.syncUp();
                    registry.openForTraffic(applicationInfoManager, registryCount);
            
                    // Register all monitoring statistics.
                    EurekaMonitors.registerAllStats();
                }
            ```

          - 其中PeerAwareInstanceRegistryImpl和PeerEurekaNodes两个类看其命名，应该和服务注册以及Eureka Server高可用有关

          - 先追踪PeerAwareInstanceRegistryImpl类，在该类有个register()方法，该方法提供了注册，并且将注册后信息同步到其他的Eureka Server服务，代码如下：

            ```
                @Override
                public void register(final InstanceInfo info, final boolean isReplication) {
                    int leaseDuration = Lease.DEFAULT_DURATION_IN_SECS;
                    if (info.getLeaseInfo() != null && info.getLeaseInfo().getDurationInSecs() > 0) {
                        leaseDuration = info.getLeaseInfo().getDurationInSecs();
                    }
                    super.register(info, leaseDuration, isReplication);
                    replicateToPeers(Action.Register, info.getAppName(), info.getId(), info, null, isReplication);
                }
            ```

          - 其中 super.register(info, leaseDuration, isReplication)方法，点击进去到子类AbstractInstanceRegistry可以发现更多细节，其中注册列表的信息被保存在一个Map中。replicateToPeers()方法，即同步到其他Eureka Server的其他Peers节点，追踪代码，发现它会遍历循环向所有的Peers节点注册，最终执行类PeerEurekaNodes的register()方法，该方法通过执行一个任务向其他节点同步该注册信息，代码如下：

            ```
                public void register(final InstanceInfo info) throws Exception {
                    long expiryTime = System.currentTimeMillis() + getLeaseRenewalOf(info);
                    batchingDispatcher.process(
                            taskId("register", info),
                            new InstanceReplicationTask(targetHost, Action.Register, info, null, true) {
                                public EurekaHttpResponse<Void> execute() {
                                    return replicationClient.register(info);
                                }
                            },
                            expiryTime
                    );
                }
            ```

          - 经过一系列的源码追踪，可以发现PeerAwareInstanceRegistryImpl的register()方法实现了服务的注册，并且向其他Eureka Server的Peer节点同步了该注册信息，那么register()方法被谁调用了呢？

          - 之前在Eureka Client的分析可以知道，Eureka Client是通过 http来向Eureka Server注册的，那么Eureka Server肯定会提供一个注册的接口给Eureka Client调用，那么PeerAwareInstanceRegistryImpl的register()方法肯定最终会被暴露的Http接口所调用

          - 定位到ApplicationResource类的addInstance ()方法，即服务注册的接口，其代码如下：

            ```
             @POST
                @Consumes({"application/json", "application/xml"})
                public Response addInstance(InstanceInfo info,
                                            @HeaderParam(PeerEurekaNode.HEADER_REPLICATION) String isReplication) {
                    logger.debug("Registering instance {} (replication={})", info.getId(), isReplication);
                    // validate that the instanceinfo contains all the necessary required fields
                    if (isBlank(info.getId())) {
                        return Response.status(400).entity("Missing instanceId").build();
                    } else if (isBlank(info.getHostName())) {
                        return Response.status(400).entity("Missing hostname").build();
                    } else if (isBlank(info.getIPAddr())) {
                        return Response.status(400).entity("Missing ip address").build();
                    } else if (isBlank(info.getAppName())) {
                        return Response.status(400).entity("Missing appName").build();
                    } else if (!appName.equals(info.getAppName())) {
                        return Response.status(400).entity("Mismatched appName, expecting " + appName + " but was " + info.getAppName()).build();
                    } else if (info.getDataCenterInfo() == null) {
                        return Response.status(400).entity("Missing dataCenterInfo").build();
                    } else if (info.getDataCenterInfo().getName() == null) {
                        return Response.status(400).entity("Missing dataCenterInfo Name").build();
                    }
            
                    // handle cases where clients may be registering with bad DataCenterInfo with missing data
                    DataCenterInfo dataCenterInfo = info.getDataCenterInfo();
                    if (dataCenterInfo instanceof UniqueIdentifier) {
                        String dataCenterInfoId = ((UniqueIdentifier) dataCenterInfo).getId();
                        if (isBlank(dataCenterInfoId)) {
                            boolean experimental = "true".equalsIgnoreCase(serverConfig.getExperimental("registration.validation.dataCenterInfoId"));
                            if (experimental) {
                                String entity = "DataCenterInfo of type " + dataCenterInfo.getClass() + " must contain a valid id";
                                return Response.status(400).entity(entity).build();
                            } else if (dataCenterInfo instanceof AmazonInfo) {
                                AmazonInfo amazonInfo = (AmazonInfo) dataCenterInfo;
                                String effectiveId = amazonInfo.get(AmazonInfo.MetaDataKey.instanceId);
                                if (effectiveId == null) {
                                    amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.instanceId.getName(), info.getId());
                                }
                            } else {
                                logger.warn("Registering DataCenterInfo of type {} without an appropriate id", dataCenterInfo.getClass());
                            }
                        }
                    }
            		//我在这里
                    registry.register(info, "true".equals(isReplication));
                    return Response.status(204).build();  // 204 to be backwards compatible
                }
            ```

  4. Renew服务续约

        - 服务续约和服务注册非常类似，通过之前的分析可以知道，服务注册在Eureka Client程序启动之后开启，并同时开启服务续约的定时任务

        - 客户端的服务续约，在DiscoveryClient类有一个服务续约的方法renew()，其源码如下：

          ```
              boolean renew() {
                  try {
                      EurekaHttpResponse<InstanceInfo> httpResponse = this.eurekaTransport.registrationClient.sendHeartBeat(this.instanceInfo.getAppName(), this.instanceInfo.getId(), this.instanceInfo, (InstanceStatus)null);
                      logger.debug("DiscoveryClient_{} - Heartbeat status: {}", this.appPathIdentifier, httpResponse.getStatusCode());
                      if (httpResponse.getStatusCode() == Status.NOT_FOUND.getStatusCode()) {
                          this.REREGISTER_COUNTER.increment();
                          logger.info("DiscoveryClient_{} - Re-registering apps/{}", this.appPathIdentifier, this.instanceInfo.getAppName());
                          long timestamp = this.instanceInfo.setIsDirtyWithTime();
                          boolean success = this.register();
                          if (success) {
                              this.instanceInfo.unsetIsDirty(timestamp);
                          }
          
                          return success;
                      } else {
                          return httpResponse.getStatusCode() == Status.OK.getStatusCode();
                      }
                  } catch (Throwable var5) {
                      logger.error("DiscoveryClient_{} - was unable to send heartbeat!", this.appPathIdentifier, var5);
                      return false;
                  }
              }
          ```

        - 服务端的服务续约，接口在eureka-core:1.9.13.jar的 com.netflix.eureka.resources包下的InstanceResource类下，接口方法为renewLease()，它是REST接口

          ```
              @PUT
              public Response renewLease(
                      @HeaderParam(PeerEurekaNode.HEADER_REPLICATION) String isReplication,
                      @QueryParam("overriddenstatus") String overriddenStatus,
                      @QueryParam("status") String status,
                      @QueryParam("lastDirtyTimestamp") String lastDirtyTimestamp) {
                  boolean isFromReplicaNode = "true".equals(isReplication);
                  boolean isSuccess = registry.renew(app.getName(), id, isFromReplicaNode);
          
                  // Not found in the registry, immediately ask for a register
                  if (!isSuccess) {
                      logger.warn("Not Found (Renew): {} - {}", app.getName(), id);
                      return Response.status(Status.NOT_FOUND).build();
                  }
                  // Check if we need to sync based on dirty time stamp, the client
                  // instance might have changed some value
                  Response response;
                  if (lastDirtyTimestamp != null && serverConfig.shouldSyncWhenTimestampDiffers()) {
                      response = this.validateDirtyTimestamp(Long.valueOf(lastDirtyTimestamp), isFromReplicaNode);
                      // Store the overridden status since the validation found out the node that replicates wins
                      if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
                              && (overriddenStatus != null)
                              && !(InstanceStatus.UNKNOWN.name().equals(overriddenStatus))
                              && isFromReplicaNode) {
                          registry.storeOverriddenStatusIfRequired(app.getAppName(), id, InstanceStatus.valueOf(overriddenStatus));
                      }
                  } else {
                      response = Response.ok().build();
                  }
                  logger.debug("Found (Renew): {} - {}; reply status={}", app.getName(), id, response.getStatus());
                  return response;
              }
          ```

        - 这里可以做一只追踪renew下去

        - 服务续约有2个参数是可以配置，即Eureka Client发送续约心跳的时间参数和Eureka Server在多长时间内没有收到心跳将实例剔除的时间参数，在默认的情况下这两个参数分别为30秒和90秒

          - eureka.instance.leaseRenewalIntervalInSeconds
          - eureka.instance.leaseExpirationDurationInSeconds

5. Eureka Client注册一个实例为什么这么慢

   - Eureka Client一启动（不是启动完成），不是立即向Eureka Server注册，它有一个延迟向服务端注册的时间，通过跟踪源码，可以发现默认的延迟时间为40秒，源码在eureka-client-1.6.2.jar的DefaultEurekaClientConfig类下，代码如下：

     ```
         @Override
         public int getInitialInstanceInfoReplicationIntervalSeconds() {
             return configInstance.getIntProperty(
                     namespace + INITIAL_REGISTRATION_REPLICATION_DELAY_KEY, 40).get();
         }
     ```

   - Eureka Server的响应缓存 Eureka Server维护每30秒更新的响应缓存,可通过更改配置eureka.server.responseCacheUpdateIntervalMs来修改。 所以即使实例刚刚注册，它也不会出现在调用/ eureka / apps REST端点的结果中

   - Eureka Server刷新缓存 Eureka客户端保留注册表信息的缓存。 该缓存每30秒更新一次（如前所述）。 因 此，客户端决定刷新其本地缓存并发现其他新注册的实例可能需要30秒

   - LoadBalancer Refresh Ribbon的负载平衡器从本地的Eureka Client获取服务注册列表信息。Ribbon本身还维护本地缓存，以避免为每个请求调用本地客户端。 此缓存每30秒刷新一次（可由ribbon.ServerListRefreshInterval配置）。 所以，可能需要30多秒才能使用新注册的实例

   - 综上几个因素，一个新注册的实例，特别是启动较快的实例（默认延迟40秒注册），不能马上被Eureka Server发现。另外，刚注册的Eureka Client也不能立即被其他服务调用，因为调用方因为各种缓存没有及时的获取到新的注册列表

6. Eureka 的自我保护模式
      - 当一个新的Eureka Server出现时，它尝试从相邻节点获取所有实例注册表信息。如果从Peer节点获取信息时出现问题，Eureka Serve会尝试其他的Peer节点。如果服务器能够成功获取所有实例，则根据该信息设置应该接收的更新阈值。如果有任何时间，Eureka Serve接收到的续约低于为该值配置的百分比（默认为15分钟内低于85％），则服务器开启自我保护模式，即不再剔除注册列表的信息
      - 这样做的好处就是，如果是Eureka Server自身的网络问题，导致Eureka Client的续约不上，Eureka Client的注册列表信息不再被删除，也就是Eureka Client还可以被其他服务消费
