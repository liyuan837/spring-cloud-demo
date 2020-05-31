package com.liyuan.demo.rest.impl;

import com.liyuan.demo.res.BaseResVo;
import com.liyuan.demo.rest.UserRest;
import org.springframework.stereotype.Component;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author liyuan
 * @Date 2020/3/3 22:15
 **/
@Component
public class UserFallbackRest implements UserRest {
    @Override
    public BaseResVo queryByName(String name) {
        return BaseResVo.builder().
                resCode(1005)
                .resMsg("用户服务不可用")
                .build();
    }
}
