package com.liyuan.demo.rest.impl;

import com.liyuan.demo.res.BaseResVo;
import com.liyuan.demo.rest.UserRest;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName UserFallbackFactory
 * @Description TODO
 * @Author liyuan
 * @Date 2020/3/3 22:15
 **/
@Slf4j
@Component
public class UserFallbackFactory implements FallbackFactory<UserRest> {
    @Override
    public UserRest create(Throwable throwable) {
        return new UserRest() {
            @Override
            public BaseResVo queryByName(String name) {
                return BaseResVo.builder().
                        resCode(1005)
                        .resMsg("用户服务不可用")
                        .build();
            }
        };
    }
}
