package com.liyuan.demo.service;

import com.liyuan.demo.exception.BusinessException;
import com.liyuan.demo.req.UserLoginDto;
import com.liyuan.demo.res.BaseResVo;
import com.liyuan.demo.rest.UserRest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author liyuan
 * @Date 2020/3/3 22:25
 **/
@Service
public class UserService {
    @Autowired
    private UserRest userRest;

    public BaseResVo login(UserLoginDto form) {
        if(StringUtils.isEmpty(form.getName()) || StringUtils.isEmpty(form.getPassword())){
            throw new BusinessException("账号或密码为空");
        }
        return userRest.queryByName(form.getName());
    }
}
