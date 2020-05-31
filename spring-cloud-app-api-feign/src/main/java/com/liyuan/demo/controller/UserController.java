package com.liyuan.demo.controller;

import com.liyuan.demo.domain.User;
import com.liyuan.demo.req.UserAddDto;
import com.liyuan.demo.req.UserLoginDto;
import com.liyuan.demo.res.BaseResVo;
import com.liyuan.demo.res.UserVo;
import com.liyuan.demo.service.UserService;
import com.liyuan.demo.utils.CopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(value = "/user", description = "")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "登录",notes = "登录",httpMethod = "POST")
    @PostMapping(value = "/login")
    public BaseResVo login(@ApiParam(value = "")@RequestBody UserLoginDto form , HttpServletRequest request) {
        log.info("login,ip={},port={}",request.getLocalAddr(),request.getLocalPort());
        return userService.login(form);
    }

    @ApiOperation(value = "注册",notes = "注册",httpMethod = "POST")
    @PostMapping(value = "/register")
    public UserVo queryCount(@RequestBody @Valid UserAddDto form) {
        User user = CopyUtil.transfer(form,User.class);
        return CopyUtil.transfer(user,UserVo.class);
    }

}