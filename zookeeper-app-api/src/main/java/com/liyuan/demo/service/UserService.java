package com.liyuan.demo.service;

import com.alibaba.fastjson.JSON;
import com.liyuan.demo.exception.BusinessException;
import com.liyuan.demo.req.UserLoginDto;
import com.liyuan.demo.res.BaseResVo;
import com.liyuan.demo.res.UserVo;
import com.liyuan.demo.rest.UserRest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author liyuan
 * @Date 2020/2/28 15:51
 **/
@Service
public class UserService {

    @Autowired
    private UserRest userRest;

    public BaseResVo login(UserLoginDto form) {
        if(StringUtils.isEmpty(form.getName()) || StringUtils.isEmpty(form.getPassword())){
            throw new BusinessException("账号或密码为空");
        }
        String result = userRest.queryByName(form);
        return JSON.parseObject(result,BaseResVo.class);
    }
}
