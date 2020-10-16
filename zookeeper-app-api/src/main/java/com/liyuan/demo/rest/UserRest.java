package com.liyuan.demo.rest;

import com.liyuan.demo.req.BaseReqDto;
import com.liyuan.demo.req.UserLoginDto;
import com.liyuan.demo.rest.base.BaseRest;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserRest
 * @Description TODO
 * @Author liyuan
 * @Date 2020/2/28 15:53
 **/
@Slf4j
@Service
public class UserRest extends BaseRest {

    /**
     * execution.isolation.thread.timeoutInMilliseconds :HystrixCommand的执行超时时间
     * @param dto
     * @return
     */
    @HystrixCommand(fallbackMethod = "restError")
    public String queryByName(UserLoginDto dto){
        Map<String,Object> paramValables = new HashMap<>(1);
        paramValables.put("name",dto.getName());
        String result = dealGetServer("http://zookeeper-provider-user/user/queryByName",paramValables);
        return result ;
    }

    /**
     *
     * 功能描述: 熔断错误
     *
     * @param reqDto
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public String restError(UserLoginDto reqDto, Throwable throwable) {
        log.error("用户数据服务不可用", throwable);

        return restError(1005, "用户数据服务不可用");
    }
}
