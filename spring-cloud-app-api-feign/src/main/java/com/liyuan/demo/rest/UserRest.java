package com.liyuan.demo.rest;

import com.liyuan.demo.res.BaseResVo;
import com.liyuan.demo.rest.impl.UserFallbackRest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName UserRest
 * @Description TODO
 * @Author liyuan
 * @Date 2020/3/3 22:09
 **/
//通过@FeignClient注解来指定服务名（spring-cloud-provider-user：不区分大小写）进而绑定服务，这边value支持占位符
@FeignClient(value = "${provider.user}",fallback = UserFallbackRest.class)
public interface UserRest {

    @GetMapping(value = "/user/queryByName")
    BaseResVo queryByName(@RequestParam("name") String name);
}
