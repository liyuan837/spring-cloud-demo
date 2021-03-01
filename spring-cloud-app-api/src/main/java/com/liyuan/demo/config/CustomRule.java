package com.liyuan.demo.config;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * 自定义ribbon负载均衡策略，分两步：
 * 1. 实现 AbstractLoadBalancerRule 类
 * 2. 重写 choose 方法
 */
public class CustomRule extends AbstractLoadBalancerRule {
    /**
     * 总共被调用的次数，目前要求每台被调用4次
     */
    private int total = 0;
    /**
     * 当前提供服务列表的索引
     */
    private int currentIndex = 0;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
    }

    /**
     * 在choose方法中，自定义我们自己的规则，返回的Server就是具体选择出来的服务
     * 自己的规则：按照轮询的规则，但是每个被轮询到的服务调用5次。
     *
     * @param o
     * @return
     */
    @Override
    public Server choose(Object o) {
        //[1] 获取负载均衡器lb
        ILoadBalancer lb = getLoadBalancer();
        if (lb == null) {
            return null;
        }

        Server server = null;
        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }
            //[2] 获取可用服务列表
            List<Server> upList = lb.getReachableServers();
            //[3] 获取所有服务列表
            List<Server> allList = lb.getAllServers();
            int serverCount = allList.size();
            if (serverCount == 0) {
                return null;
            }

            //[4] 若调用次数小于4次，一直调用可用服务列表中索引为 currentIndex 的服务
            if (total < 4) {
                //这边做数组越界判断是为了防止，服务提供者如果一个下线了，那个这边的currentIndex可能会取到不存在的服务
                if(upList.size() < currentIndex){
                    server = upList.get(currentIndex);
                    total++;
                }else{
                    currentIndex = currentIndex % upList.size();
                    server = upList.get(currentIndex);
                    total = 0;
                }
            } else {
                // 到了4次之后，服务列表中的索引值++，表示下一个调用下一个服务
                total = 0;
                currentIndex++;
                // 当索引大于可用服务列表的size时，要重新从头开始
                currentIndex = currentIndex % upList.size();

                if (server == null) {
                    Thread.yield();
                    continue;
                }

                if (server.isAlive()) {
                    return (server);
                }

                server = null;
                Thread.yield();
            }
        }
        return server;
    }
}
